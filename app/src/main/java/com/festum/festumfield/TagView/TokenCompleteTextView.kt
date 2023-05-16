package com.festum.festumfield.TagView;

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.Layout
import android.text.NoCopySpan
import android.text.Selection
import android.text.SpanWatcher
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.UiThread
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class TokenCompleteTextView<T: Any> : AppCompatAutoCompleteTextView, OnEditorActionListener,
    ViewSpan.Layout {
    enum class TokenClickStyle(val isSelectable: Boolean) {
        None(false),
        Delete(false),
        Select(true),
        SelectDeselect(true);

    }

    private var tokenizer: Tokenizer? = null
    private var selectedObject: T? = null
    private var listener: TokenListener<T>? = null
    private var spanWatcher: TokenSpanWatcher = TokenSpanWatcher()
    private var textWatcher: TokenTextWatcher = TokenTextWatcher()
    private var countSpan: CountSpan = CountSpan()
    private var hiddenContent: SpannableStringBuilder? = null
    private var tokenClickStyle: TokenClickStyle? = TokenClickStyle.None
    private var prefix: CharSequence? = null
    private var lastLayout: Layout? = null
    private var initialized = false
    private var performBestGuess = true
    private var preventFreeFormText = true
    private var savingState = false
    private var shouldFocusNext = false
    private var allowCollapse = true
    private var internalEditInProgress = false
    private var inBatchEditAPI26to29Workaround = false
    private var tokenLimit = -1

    private var ignoreNextTextCommit = false

    @Transient
    private var lastCompletionText: String? = null

    private val hintVisible: Boolean
        get() {
            return text.getSpans(0, text.length, HintSpan::class.java).isNotEmpty()
        }

    protected open fun addListeners() {
        val text = text
        if (text != null) {
            text.setSpan(spanWatcher, 0, text.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            addTextChangedListener(textWatcher)
        }
    }

    protected open fun removeListeners() {
        val text = text
        if (text != null) {
            val spanWatchers = text.getSpans(0, text.length, TokenSpanWatcher::class.java)
            for (watcher in spanWatchers) {
                text.removeSpan(watcher)
            }
            removeTextChangedListener(textWatcher)
        }
    }

    private fun init() {
        if (initialized) return

        setTokenizer(CharacterTokenizer(listOf(',', ';'), ","))

        addListeners()
        setTextIsSelectable(false)
        isLongClickable = false

        inputType = inputType or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
        setHorizontallyScrolling(false)

        setOnEditorActionListener(this)

        filters =
            arrayOf(InputFilter { source, _, _, _, destinationStart, destinationEnd ->
                if (internalEditInProgress) {
                    return@InputFilter null
                }
                if (tokenLimit != -1 && objects.size == tokenLimit) {
                    return@InputFilter ""
                }

                if (tokenizer!!.containsTokenTerminator(source)) {
                    if (preventFreeFormText || currentCompletionText().isNotEmpty()) {
                        performCompletion()
                        return@InputFilter ""
                    }
                }

                prefix?.also { prefix ->
                    if (destinationStart < prefix.length) {
                        if (destinationStart == 0 && destinationEnd == 0) {
                            return@InputFilter null
                        } else return@InputFilter if (destinationEnd <= prefix.length) {
                            prefix.subSequence(destinationStart, destinationEnd)
                        } else {
                            prefix.subSequence(destinationStart, prefix.length)
                        }
                    }
                }
                null
            })
        initialized = true
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        val filter = filter
        filter?.filter(currentCompletionText(), this)
    }

    fun setTokenizer(t: Tokenizer) {
        tokenizer = t
    }

    fun setTokenClickStyle(cStyle: TokenClickStyle) {
        tokenClickStyle = cStyle
    }

    fun setTokenListener(l: TokenListener<T>?) {
        listener = l
    }

    open fun shouldIgnoreToken(token: T): Boolean {
        return false
    }

    open fun isTokenRemovable(@Suppress("unused_parameter") token: T): Boolean {
        return true
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setPrefix(p: CharSequence) {
        val prevPrefix = prefix
        prefix = p
        val text = text
        if (text != null) {
            internalEditInProgress = true
            if (prevPrefix.isNullOrEmpty()) {
                text.insert(0, p)
            } else {
                text.replace(0, prevPrefix.length, p)
            }
            internalEditInProgress = false
        }
        updateHint()
    }

    val objects: List<T>
        get() {
            val objects = ArrayList<T>()
            var text = text
            if (hiddenContent != null) {
                text = hiddenContent
            }
            for (span in text.getSpans(0, text.length, TokenImageSpan::class.java)) {
                @Suppress("unchecked_cast")
                objects.add(span.token as T)
            }
            return objects
        }

    @Suppress("unused")
    fun setTokenLimit(tokenLimit: Int) {
        this.tokenLimit = tokenLimit
    }

    protected abstract fun getViewForObject(obj: T): View?

    protected abstract fun defaultObject(completionText: String): T?

    @Suppress("MemberVisibilityCanBePrivate")
    open val textForAccessibility: CharSequence
        get() {
            if (objects.isEmpty()) {
                return text
            }
            var description = SpannableStringBuilder()
            val text = text
            var selectionStart = -1
            var selectionEnd = -1
            var i: Int
            i = 0
            while (i < text.length) {

                val origSelectionStart = Selection.getSelectionStart(text)
                if (i == origSelectionStart) {
                    selectionStart = description.length
                }
                val origSelectionEnd = Selection.getSelectionEnd(text)
                if (i == origSelectionEnd) {
                    selectionEnd = description.length
                }
                val tokens = text.getSpans(i, i, TokenImageSpan::class.java)
                if (tokens.isNotEmpty()) {
                    val token = tokens[0]
                    description =
                        description.append(tokenizer!!.wrapTokenValue(token.token.toString()))
                    i = text.getSpanEnd(token)
                    ++i
                    continue
                }
                description = description.append(text.subSequence(i, i + 1))
                ++i
            }
            val origSelectionStart = Selection.getSelectionStart(text)
            if (i == origSelectionStart) {
                selectionStart = description.length
            }
            val origSelectionEnd = Selection.getSelectionEnd(text)
            if (i == origSelectionEnd) {
                selectionEnd = description.length
            }
            if (selectionStart >= 0 && selectionEnd >= 0) {
                Selection.setSelection(description, selectionStart, selectionEnd)
            }
            return description
        }

    @Suppress("unused")
    fun clearCompletionText() {
        if (currentCompletionText().isEmpty()) {
            return
        }
        val currentRange = currentCandidateTokenRange
        internalEditInProgress = true
        text.delete(currentRange.start, currentRange.end)
        internalEditInProgress = false
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            val text = textForAccessibility
            event.fromIndex = Selection.getSelectionStart(text)
            event.toIndex = Selection.getSelectionEnd(text)
            event.itemCount = text.length
        }
    }
    private val currentCandidateTokenRange: Range
        get() {
            val editable = text
            val cursorEndPosition = selectionEnd
            var candidateStringStart = prefix?.length ?: 0
            var candidateStringEnd = editable.length
            if (hintVisible) {
                candidateStringEnd = candidateStringStart
            }

            val spans = editable.getSpans(prefix?.length ?: 0, editable.length, TokenImageSpan::class.java)
            for (span in spans) {
                val spanEnd = editable.getSpanEnd(span)
                if (spanEnd in (candidateStringStart + 1)..cursorEndPosition) {
                    candidateStringStart = spanEnd
                }
                val spanStart = editable.getSpanStart(span)
                if (candidateStringEnd > spanStart && cursorEndPosition <= spanEnd) {
                    candidateStringEnd = spanStart
                }
            }
            val tokenRanges =
                tokenizer!!.findTokenRanges(editable, candidateStringStart, candidateStringEnd)
            for (range in tokenRanges) {
                @Suppress("unused")
                if (range.start <= cursorEndPosition && cursorEndPosition <= range.end) {
                    return range
                }
            }
            return Range(cursorEndPosition, cursorEndPosition)
        }
    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun tokenToString(token: T): CharSequence {
        return token.toString()
    }

    protected open fun currentCompletionText(): String {
        if (hintVisible) return ""
        val editable = text
        val currentRange = currentCandidateTokenRange
        val result = TextUtils.substring(editable, currentRange.start, currentRange.end)
        Log.d(TAG, "Current completion text: $result")
        return result
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun maxTextWidth(): Float {
        return (width - paddingLeft - paddingRight).toFloat()
    }

    override val maxViewSpanWidth: Int
        get() = maxTextWidth().toInt()

    fun redrawTokens() {
        val text = text ?: return
        val textLength = text.length
        val dummySpans = text.getSpans(0, textLength, DummySpan::class.java)
        if (dummySpans.isNotEmpty()) {
            text.removeSpan(DummySpan.INSTANCE)
        } else {
            text.setSpan(
                DummySpan.INSTANCE,
                0,
                textLength,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
    }

    override fun enoughToFilter(): Boolean {
        if (tokenizer == null || hintVisible) {
            return false
        }
        val cursorPosition = selectionEnd
        if (cursorPosition < 0) {
            return false
        }
        val currentCandidateRange = currentCandidateTokenRange

        @Suppress("MemberVisibilityCanBePrivate")
        return currentCandidateRange.length() >= threshold.coerceAtLeast(1)
    }

    override fun performCompletion() {
        if ((adapter == null || listSelection == ListView.INVALID_POSITION) && enoughToFilter()) {
            val bestGuess: Any? = if (adapter != null && adapter.count > 0 && performBestGuess) {
                adapter.getItem(0)
            } else {
                defaultObject(currentCompletionText())
            }
            replaceText(convertSelectionToString(bestGuess))
        } else {
            super.performCompletion()
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val superConn = super.onCreateInputConnection(outAttrs)
        val conn = TokenInputConnection(superConn, true)
        outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        outAttrs.imeOptions = outAttrs.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        return conn
    }

    private fun handleDone() {
        performCompletion()

        val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val handled = super.onKeyUp(keyCode, event)
        if (shouldFocusNext) {
            shouldFocusNext = false
            handleDone()
        }
        return handled
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var handled = false
        when (keyCode) {
            KeyEvent.KEYCODE_TAB, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> if (event?.hasNoModifiers() == true) {
                shouldFocusNext = true
                handled = true
            }
            KeyEvent.KEYCODE_DEL -> handled = !canDeleteSelection(1) || deleteSelectedObject()
        }
        return handled || super.onKeyDown(keyCode, event)
    }

    private fun deleteSelectedObject(): Boolean {
        if (tokenClickStyle?.isSelectable == true) {
            val text = text ?: return false
            @Suppress("unchecked_cast")
            val spans: Array<TokenImageSpan> =
                text.getSpans(0, text.length, TokenImageSpan::class.java) as Array<TokenImageSpan>
            for (span in spans) {
                if (span.view.isSelected) {
                    removeSpan(text, span)
                    return true
                }
            }
        }
        return false
    }

    override fun onEditorAction(view: TextView, action: Int, keyEvent: KeyEvent?): Boolean {
        if (action == EditorInfo.IME_ACTION_DONE) {
            handleDone()
            return true
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val text = text
        var handled = false
        if (tokenClickStyle == TokenClickStyle.None) {
            handled = super.onTouchEvent(event)
        }
        if (isFocused && text != null && lastLayout != null && action == MotionEvent.ACTION_UP) {
            val offset = getOffsetForPosition(event.x, event.y)
            if (offset != -1) {
                @Suppress("unchecked_cast")
                val links: Array<TokenImageSpan> =
                    text.getSpans(offset, offset, TokenImageSpan::class.java) as Array<TokenImageSpan>
                if (links.isNotEmpty()) {
                    links[0].onClick()
                    handled = true
                } else {
                    clearSelections()
                }
            }
        }
        if (!handled && tokenClickStyle != TokenClickStyle.None) {
            handled = super.onTouchEvent(event)
        }
        return handled
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        var selectionStart = selStart
        if (hintVisible) {
            selectionStart = 0
        }
        val selectionEnd = selectionStart
        if (tokenClickStyle?.isSelectable == true) {
            val text = text
            if (text != null) {
                clearSelections()
            }
        }
        if (selectionStart < prefix?.length ?: 0 || selectionEnd < prefix?.length ?: 0) {
            setSelection(prefix?.length ?: 0)
        } else {
            val text = text
            if (text != null) {
                @Suppress("unchecked_cast")
                val spans: Array<TokenImageSpan> =
                    text.getSpans(selectionStart, selectionEnd, TokenImageSpan::class.java) as Array<TokenImageSpan>
                for (span in spans) {
                    val spanEnd = text.getSpanEnd(span)
                    if (selectionStart <= spanEnd && text.getSpanStart(span) < selectionStart) {
                        if (spanEnd == text.length) setSelection(spanEnd) else setSelection(spanEnd + 1)
                        return
                    }
                }
            }
            super.onSelectionChanged(selectionStart, selectionEnd)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        lastLayout = layout
    }

    open fun performCollapse(hasFocus: Boolean) {
        internalEditInProgress = true
        if (!hasFocus) {
            val text = text
            if (text != null && hiddenContent == null && lastLayout != null) {

                text.removeSpan(spanWatcher)
                val temp = if (preventFreeFormText) countSpan else null
                val ellipsized = SpanUtils.ellipsizeWithSpans(
                    prefix, temp, objects.size,
                    lastLayout!!.paint, text, maxTextWidth()
                )
                if (ellipsized != null) {
                    hiddenContent = SpannableStringBuilder(text)
                    setText(ellipsized)
                    TextUtils.copySpansFrom(
                        ellipsized, 0, ellipsized.length,
                        TokenImageSpan::class.java, getText(), 0
                    )
                    TextUtils.copySpansFrom(
                        text, 0, hiddenContent!!.length,
                        TokenImageSpan::class.java, hiddenContent, 0
                    )
                    hiddenContent!!.setSpan(
                        spanWatcher,
                        0,
                        hiddenContent!!.length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                } else {
                    getText().setSpan(
                        spanWatcher,
                        0,
                        getText().length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
            }
        } else {
            if (hiddenContent != null) {
                text = hiddenContent
                TextUtils.copySpansFrom(
                    hiddenContent, 0, hiddenContent!!.length,
                    TokenImageSpan::class.java, text, 0
                )
                hiddenContent = null
                if (hintVisible) {
                    setSelection(prefix?.length ?: 0)
                } else {
                    post { setSelection(text.length) }
                }
                @Suppress("unchecked_cast")
                val watchers: Array<TokenSpanWatcher> =
                    text.getSpans(0, text.length, TokenSpanWatcher::class.java) as Array<TokenSpanWatcher>
                if (watchers.isEmpty()) {
                    text.setSpan(spanWatcher, 0, text.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }
        internalEditInProgress = false
    }

    public override fun onFocusChanged(hasFocus: Boolean, direction: Int, previous: Rect?) {
        super.onFocusChanged(hasFocus, direction, previous)

        clearSelections()

        if (allowCollapse) performCollapse(hasFocus)
    }

    override fun convertSelectionToString(selectedObject: Any?): CharSequence {
        @Suppress("unchecked_cast")
        this.selectedObject = selectedObject as T?
        return ""
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun buildSpanForObject(obj: T?): TokenImageSpan? {
        if (obj == null) {
            return null
        }
        return getViewForObject(obj)?.let { TokenImageSpan(it, obj) }
    }

    override fun replaceText(ignore: CharSequence) {
        clearComposingText()

        if (selectedObject?.toString().isNullOrEmpty()) return
        val tokenSpan = buildSpanForObject(selectedObject)
        val editable = text
        val candidateRange = currentCandidateTokenRange
        val original = TextUtils.substring(editable, candidateRange.start, candidateRange.end)

        if (original.isNotEmpty()) {
            lastCompletionText = original
        }
        if (editable != null) {
            internalEditInProgress = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ignoreNextTextCommit = true
            }
            if (tokenSpan == null) {
                editable.replace(candidateRange.start, candidateRange.end, "")
            } else if (shouldIgnoreToken(tokenSpan.token)) {
                editable.replace(candidateRange.start, candidateRange.end, "")
                if (listener != null) {
                    listener?.onTokenIgnored(tokenSpan.token)
                }
            } else {
                val ssb = SpannableStringBuilder(tokenizer!!.wrapTokenValue(tokenToString(tokenSpan.token)))
                editable.replace(candidateRange.start, candidateRange.end, ssb)
                editable.setSpan(
                    tokenSpan,
                    candidateRange.start,
                    candidateRange.start + ssb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                editable.insert(candidateRange.start + ssb.length, " ")
            }
            internalEditInProgress = false
        }
    }

    override fun extractText(request: ExtractedTextRequest, outText: ExtractedText): Boolean {
        return try {
            super.extractText(request, outText)
        } catch (ex: IndexOutOfBoundsException) {
            Log.d(TAG, "extractText hit IndexOutOfBoundsException. This may be normal.", ex)
            false
        }
    }

    @UiThread
    fun addObjectSync(obj: T) {
        if (shouldIgnoreToken(obj)) {
            if (listener != null) {
                listener?.onTokenIgnored(obj)
            }
            return
        }
        if (tokenLimit != -1 && objects.size == tokenLimit) return
        buildSpanForObject(obj)?.also { insertSpan(it) }
        if (text != null && isFocused) setSelection(text.length)
    }

    fun addObjectAsync(obj: T) {
        post { addObjectSync(obj) }
    }

    @UiThread
    fun removeObjectSync(obj: T) {
        val texts = ArrayList<Editable>()
        hiddenContent?.also { texts.add(it) }
        if (text != null) {
            texts.add(text)
        }

        for (text in texts) {
            @Suppress("unchecked_cast")
            val spans: Array<TokenImageSpan> =
                text.getSpans(0, text.length, TokenImageSpan::class.java) as Array<TokenImageSpan>
            for (span in spans) {
                if (span.token == obj) {
                    removeSpan(text, span)
                }
            }
        }
        updateCountSpan()
    }

    fun removeObjectAsync(obj: T) {
        post { removeObjectSync(obj) }
    }

    fun clearAsync() {
        post {
            for (obj in objects) {
                removeObjectSync(obj)
            }
        }
    }

    private fun updateCountSpan() {
        if (!preventFreeFormText) {
            return
        }
        val text = text
        val visibleCount = getText().getSpans(0, getText().length, TokenImageSpan::class.java).size
        countSpan.setCount(objects.size - visibleCount)
        val spannedCountText = SpannableStringBuilder(countSpan.countText)
        spannedCountText.setSpan(
            countSpan,
            0,
            spannedCountText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        internalEditInProgress = true
        val countStart = text.getSpanStart(countSpan)
        if (countStart != -1) {
            text.replace(countStart, text.getSpanEnd(countSpan), spannedCountText)
        } else {
            text.append(spannedCountText)
        }
        internalEditInProgress = false
    }

    private fun removeSpan(text: Editable, span: TokenImageSpan) {
        var end = text.getSpanEnd(span)
        if (end < text.length && text[end] == ' ') {
            end += 1
        }
        internalEditInProgress = true
        text.delete(text.getSpanStart(span), end)
        internalEditInProgress = false
        if (allowCollapse && !isFocused) {
            updateCountSpan()
        }
    }

    private fun insertSpan(tokenSpan: TokenImageSpan) {
        val ssb = tokenizer!!.wrapTokenValue(tokenToString(tokenSpan.token))
        val editable = text ?: return

        if (hiddenContent == null) {
            internalEditInProgress = true
            var offset = editable.length
            if (hintVisible) {
                offset = prefix?.length ?: 0
            } else {
                val currentRange = currentCandidateTokenRange
                if (currentRange.length() > 0) {
                    offset = currentRange.start
                }
            }
            editable.insert(offset, ssb)
            editable.insert(offset + ssb.length, " ")
            editable.setSpan(
                tokenSpan,
                offset,
                offset + ssb.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            internalEditInProgress = false
        } else {
            val tokenText = tokenizer!!.wrapTokenValue(
                tokenToString(
                    tokenSpan.token
                )
            )
            val start = hiddenContent!!.length
            hiddenContent!!.append(tokenText)
            hiddenContent!!.append(" ")
            hiddenContent!!.setSpan(
                tokenSpan,
                start,
                start + tokenText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            updateCountSpan()
        }
    }

    private fun updateHint() {
        val text = text
        val hintText = hint
        if (text == null || hintText == null) {
            return
        }

        if (prefix?.isNotEmpty() == true) {
            val hints = text.getSpans(0, text.length, HintSpan::class.java)
            var hint: HintSpan? = null
            var testLength = prefix?.length ?: 0
            if (hints.isNotEmpty()) {
                hint = hints[0]
                testLength += text.getSpanEnd(hint) - text.getSpanStart(hint)
            }
            if (text.length == testLength) {
                if (hint != null) {
                    return
                }

                val tf = typeface
                var style = Typeface.NORMAL
                if (tf != null) {
                    style = tf.style
                }
                val colors = hintTextColors
                val hintSpan = HintSpan(null, style, textSize.toInt(), colors, colors)
                internalEditInProgress = true
                val spannedHint = SpannableString(hintText)
                spannedHint.setSpan(hintSpan, 0, spannedHint.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text.insert(prefix?.length ?: 0, spannedHint)
                internalEditInProgress = false
                setSelection(prefix?.length ?: 0)
            } else {
                if (hint == null) {
                    return
                }


                val sStart = text.getSpanStart(hint)
                val sEnd = text.getSpanEnd(hint)
                internalEditInProgress = true
                text.removeSpan(hint)
                text.replace(sStart, sEnd, "")
                setSelection(sStart)
                internalEditInProgress = false
            }
        }
    }

    private fun clearSelections() {
        if (tokenClickStyle?.isSelectable != true) return
        val text = text ?: return
        @Suppress("unchecked_cast")
        val tokens: Array<TokenImageSpan> =
            text.getSpans(0, text.length, TokenImageSpan::class.java) as Array<TokenImageSpan>
        var shouldRedrawTokens = false
        for (token in tokens) {
            if (token.view.isSelected) {
                token.view.isSelected = false
                shouldRedrawTokens = true
            }
        }
        if (shouldRedrawTokens) {
            redrawTokens()
        }
    }

    inner class TokenImageSpan(d: View, val token: T) : ViewSpan(d, this@TokenCompleteTextView),
        NoCopySpan {
        fun onClick() {
            val text = text ?: return
            when (tokenClickStyle) {
                TokenClickStyle.Select, TokenClickStyle.SelectDeselect -> {
                    if (!view.isSelected) {
                        clearSelections()
                        view.isSelected = true
                        redrawTokens()
                    } else if (tokenClickStyle == TokenClickStyle.SelectDeselect || !isTokenRemovable(token)) {
                        view.isSelected = false
                        redrawTokens()
                    } else if (isTokenRemovable(token)) {
                        removeSpan(text, this)
                    }
                }
                TokenClickStyle.Delete -> if (isTokenRemovable(token)) {
                    removeSpan(text, this)
                }
                TokenClickStyle.None -> if (selectionStart != text.getSpanEnd(this)) {
                    setSelection(text.getSpanEnd(this))
                }
                else -> {}
            }
        }
    }

    interface TokenListener<T> {
        fun onTokenAdded(token: T)
        fun onTokenRemoved(token: T)
        fun onTokenIgnored(token: T)
    }

    private inner class TokenSpanWatcher : SpanWatcher {
        override fun onSpanAdded(text: Spannable, what: Any, start: Int, end: Int) {
            if (what is TokenCompleteTextView<*>.TokenImageSpan && !savingState) {

                if (!isFocused && allowCollapse) performCollapse(false)
                @Suppress("unchecked_cast")
                if (listener != null) listener?.onTokenAdded(what.token as T)
            }
        }

        override fun onSpanRemoved(text: Spannable, what: Any, start: Int, end: Int) {
            if (what is TokenCompleteTextView<*>.TokenImageSpan && !savingState) {
                @Suppress("unchecked_cast")
                if (listener != null) listener?.onTokenRemoved(what.token as T)
            }
        }

        override fun onSpanChanged(
            text: Spannable, what: Any,
            oldStart: Int, oldEnd: Int, newStart: Int, newEnd: Int
        ) {
        }
    }

    private inner class TokenTextWatcher : TextWatcher {
        var spansToRemove = ArrayList<TokenImageSpan>()
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (internalEditInProgress || ignoreNextTextCommit) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (s is SpannableStringBuilder && s.textWatcherDepth > 1) return
            }

            if (count > 0 && text != null) {
                val text = text
                val end = start + count
                @Suppress("unchecked_cast")
                val spans = text.getSpans(start, end, TokenImageSpan::class.java) as Array<TokenImageSpan>

                val spansToRemove = ArrayList<TokenImageSpan>()
                for (token in spans) {
                    if (text.getSpanStart(token) < end && start < text.getSpanEnd(token)) {
                        spansToRemove.add(token)
                    }
                }
                this.spansToRemove = spansToRemove
            }
        }

        override fun afterTextChanged(text: Editable) {
            if (!internalEditInProgress) {
                val spansCopy = ArrayList(spansToRemove)
                spansToRemove.clear()
                for (token in spansCopy) {
                    if (text.getSpanStart(token) != -1 && text.getSpanEnd(token) != -1) {
                        removeSpan(text, token)
                    }
                }
                ignoreNextTextCommit = false
            }

            clearSelections()

            if (!inBatchEditAPI26to29Workaround) {
                updateHint()
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun getSerializableObjects(): List<Serializable> {
        val serializables = ArrayList<Serializable>()
        for (obj in objects) {
            if (obj is Serializable) {
                serializables.add(obj as Serializable)
            } else {
                Log.e(TAG, "Unable to save '$obj'")
            }
        }
        if (serializables.size != objects.size) {
            val message = """
            You should make your objects Serializable or Parcelable or
            override getSerializableObjects and convertSerializableArrayToObjectArray
            """.trimIndent()
            Log.e(TAG, message)
        }
        return serializables
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun convertSerializableObjectsToTypedObjects(s: List<*>?): List<T>? {
        @Suppress("unchecked_cast")
        return s as List<T>?
    }

    private fun reifyParameterizedTypeClass(): Class<*> {

        var viewClass: Class<*> = javaClass
        while (viewClass.superclass != TokenCompleteTextView::class.java) {
            viewClass = viewClass.superclass as Class<*>
        }

        val superclass = viewClass.genericSuperclass as ParameterizedType
        val type = superclass.actualTypeArguments[0]
        return type as Class<*>
    }

    override fun onSaveInstanceState(): Parcelable {
        removeListeners()

        savingState = true
        val superState = super.onSaveInstanceState()
        savingState = false
        val state = SavedState(superState)
        state.prefix = prefix
        state.allowCollapse = allowCollapse
        state.performBestGuess = performBestGuess
        state.preventFreeFormText = preventFreeFormText
        state.tokenClickStyle = tokenClickStyle
        val parameterizedClass = reifyParameterizedTypeClass()
        if (Parcelable::class.java.isAssignableFrom(parameterizedClass)) {
            state.parcelableClassName = parameterizedClass.name
            state.baseObjects = objects
        } else {
            state.parcelableClassName = SavedState.SERIALIZABLE_PLACEHOLDER
            state.baseObjects = getSerializableObjects()
        }
        state.tokenizer = tokenizer

        addListeners()
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        internalEditInProgress = true
        setText(state.prefix)
        prefix = state.prefix
        internalEditInProgress = false
        updateHint()
        allowCollapse = state.allowCollapse
        performBestGuess = state.performBestGuess
        preventFreeFormText = state.preventFreeFormText
        tokenClickStyle = state.tokenClickStyle
        tokenizer = state.tokenizer
        addListeners()
        val objects: List<T>? = if (SavedState.SERIALIZABLE_PLACEHOLDER == state.parcelableClassName) {
            convertSerializableObjectsToTypedObjects(state.baseObjects)
        } else {
            @Suppress("unchecked_cast")
            state.baseObjects as List<T>?
        }

        if (objects != null) {
            for (obj in objects) {
                addObjectSync(obj)
            }
        }

        if (!isFocused && allowCollapse) {
            post {
                performCollapse(isFocused)
            }
        }
    }

    private class SavedState : BaseSavedState {
        var prefix: CharSequence? = null
        var allowCollapse = false
        var performBestGuess = false
        var preventFreeFormText = false
        var tokenClickStyle: TokenClickStyle? = null
        var parcelableClassName: String = SERIALIZABLE_PLACEHOLDER
        var baseObjects: List<*>? = null
        var tokenizerClassName: String? = null
        var tokenizer: Tokenizer? = null

        constructor(parcel: Parcel) : super(parcel) {
            prefix = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
            allowCollapse = parcel.readInt() != 0
            performBestGuess = parcel.readInt() != 0
            preventFreeFormText = parcel.readInt() != 0
            tokenClickStyle = TokenClickStyle.values()[parcel.readInt()]
            parcelableClassName = parcel.readString() ?: SERIALIZABLE_PLACEHOLDER
            baseObjects = if (SERIALIZABLE_PLACEHOLDER == parcelableClassName) {
                parcel.readSerializable() as ArrayList<*>
            } else {
                try {
                    val loader = Class.forName(parcelableClassName).classLoader
                    parcel.readArrayList(loader)
                } catch (ex: ClassNotFoundException) {
                    throw RuntimeException(ex)
                }
            }
            tokenizerClassName = parcel.readString()
            tokenizer = try {
                val loader = Class.forName(tokenizerClassName!!).classLoader
                parcel.readParcelable(loader)
            } catch (ex: ClassNotFoundException) {
                throw RuntimeException(ex)
            }
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(prefix, out, 0)
            out.writeInt(if (allowCollapse) 1 else 0)
            out.writeInt(if (performBestGuess) 1 else 0)
            out.writeInt(if (preventFreeFormText) 1 else 0)
            out.writeInt((tokenClickStyle ?: TokenClickStyle.None).ordinal)
            if (SERIALIZABLE_PLACEHOLDER == parcelableClassName) {
                out.writeString(SERIALIZABLE_PLACEHOLDER)
                out.writeSerializable(baseObjects as Serializable?)
            } else {
                out.writeString(parcelableClassName)
                out.writeList(baseObjects)
            }
            out.writeString(tokenizer!!.javaClass.canonicalName)
            out.writeParcelable(tokenizer, 0)
        }

        override fun toString(): String {
            val str = ("TokenCompleteTextView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " tokens=" + baseObjects)
            return "$str}"
        }

        companion object {
            const val SERIALIZABLE_PLACEHOLDER = "Serializable"
            @Suppress("unused")
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    fun canDeleteSelection(beforeLength: Int): Boolean {
        if (objects.isEmpty()) return true

        val endSelection = selectionEnd
        val startSelection = if (beforeLength == 1) selectionStart else endSelection - beforeLength
        val text = text
        val spans = text.getSpans(0, text.length, TokenImageSpan::class.java)

        for (span in spans) {
            val startTokenSelection = text.getSpanStart(span)
            val endTokenSelection = text.getSpanEnd(span)

            @Suppress("unchecked_cast")
            if (isTokenRemovable(span.token as T)) continue
            if (startSelection == endSelection) {
                if (endTokenSelection + 1 == endSelection) {
                    return false
                }
            } else {
                if (startSelection <= startTokenSelection
                    && endTokenSelection + 1 <= endSelection
                ) {
                    return false
                }
            }
        }
        return true
    }

    private inner class TokenInputConnection(
        target: InputConnection?,
        mutable: Boolean
    ) : InputConnectionWrapper(target, mutable) {

        private val needsWorkaround: Boolean
            get() {
                return Build.VERSION_CODES.O <= Build.VERSION.SDK_INT  &&
                        Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q

            }

        override fun beginBatchEdit(): Boolean {
            if (needsWorkaround) {
                inBatchEditAPI26to29Workaround = true
            }
            return super.beginBatchEdit()
        }

        override fun endBatchEdit(): Boolean {
            val result = super.endBatchEdit()
            if (needsWorkaround) {
                inBatchEditAPI26to29Workaround = false
                post { updateHint() }
            }
            return result
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            var fixedBeforeLength = beforeLength
            if (!canDeleteSelection(fixedBeforeLength)) return false

            if (selectionStart <= prefix?.length ?: 0) {
                fixedBeforeLength = 0
                return deleteSelectedObject() || super.deleteSurroundingText(
                    fixedBeforeLength,
                    afterLength
                )
            }
            return super.deleteSurroundingText(fixedBeforeLength, afterLength)
        }

        override fun setComposingRegion(start: Int, end: Int): Boolean {
            var fixedStart = start
            var fixedEnd = end
            if (hintVisible) {
                fixedEnd = 0
                fixedStart = fixedEnd
            }
            return super.setComposingRegion(fixedStart, fixedEnd)
        }

        override fun setComposingText(text: CharSequence, newCursorPosition: Int): Boolean {
            var fixedText: CharSequence? = text
            val hint = hint
            if (hint != null && fixedText != null) {
                val firstWord = hint.toString().trim { it <= ' ' }.split(" ").toTypedArray()[0]
                if (firstWord.isNotEmpty() && firstWord == fixedText.toString()) {
                    fixedText = ""
                }
            }

            lastCompletionText?.also { lastCompletion ->
                fixedText?.also { fixed ->
                    if (fixed.length == lastCompletion.length + 1 && fixed.toString().startsWith(lastCompletion)) {
                        fixedText = fixed.subSequence(fixed.length - 1, fixed.length)
                        lastCompletionText = null
                    }
                }
            }
            return super.setComposingText(fixedText, newCursorPosition)
        }
    }

    companion object {
        const val TAG = "TokenAutoComplete"
    }
}