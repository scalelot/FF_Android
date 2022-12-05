package com.example.friendfield.TagView;

import android.os.Parcelable

interface Tokenizer : Parcelable {
    fun findTokenRanges(charSequence: CharSequence, start: Int, end: Int): List<Range>

    fun wrapTokenValue(unwrappedTokenValue: CharSequence): CharSequence

    fun containsTokenTerminator(charSequence: CharSequence): Boolean
}