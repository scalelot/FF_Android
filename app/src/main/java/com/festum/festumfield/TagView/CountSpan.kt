package com.festum.festumfield.TagView;

import android.text.Layout
import android.text.TextPaint
import android.text.style.CharacterStyle
import java.util.*

class CountSpan : CharacterStyle() {
    var countText = ""
        private set

    override fun updateDrawState(textPaint: TextPaint) {
    }

    fun setCount(c: Int) {
        countText = if (c > 0) {
            String.format(Locale.getDefault(), " +%d", c)
        } else {
            ""
        }
    }

    fun getCountTextWidthForPaint(paint: TextPaint?): Float {
        return Layout.getDesiredWidth(countText, 0, countText.length, paint)
    }
}