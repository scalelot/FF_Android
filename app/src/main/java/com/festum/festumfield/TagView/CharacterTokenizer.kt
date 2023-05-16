package com.festum.festumfield.TagView;

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@SuppressLint("ParcelCreator")
open class CharacterTokenizer(private val splitChar: List<Char>, private val tokenTerminator: String) :
    Tokenizer {
    override fun containsTokenTerminator(charSequence: CharSequence): Boolean {
        for (element in charSequence) {
            if (splitChar.contains(element)) {
                return true
            }
        }
        return false
    }

    override fun findTokenRanges(charSequence: CharSequence, start: Int, end: Int): List<Range> {
        val result = ArrayList<Range>()
        if (start == end) {
            return result
        }
        var tokenStart = start
        for (cursor in start until end) {
            val character = charSequence[cursor]

            if (tokenStart == cursor && Character.isWhitespace(character)) {
                tokenStart = cursor + 1
            }

            if (splitChar.contains(character) || cursor == end - 1) {
                val hasTokenContent =
                    cursor > tokenStart ||
                            cursor == tokenStart && !splitChar.contains(character)
                if (hasTokenContent) {
                    result.add(Range(tokenStart, cursor + 1))
                }
                tokenStart = cursor + 1
            }
        }
        return result
    }

    override fun wrapTokenValue(unwrappedTokenValue: CharSequence): CharSequence {
        val wrappedText: CharSequence = unwrappedTokenValue.toString() + tokenTerminator
        return if (unwrappedTokenValue is Spanned) {
            val sp = SpannableString(wrappedText)
            TextUtils.copySpansFrom(
                unwrappedTokenValue, 0, unwrappedTokenValue.length,
                Any::class.java, sp, 0
            )
            sp
        } else {
            wrappedText
        }
    }
}