package com.festum.festumfield.TagView;

import android.content.res.ColorStateList
import android.text.style.TextAppearanceSpan

internal class HintSpan(
    family: String?, style: Int, size: Int, color: ColorStateList?, linkColor: ColorStateList?
) : TextAppearanceSpan(family, style, size, color, linkColor)