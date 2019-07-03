package com.arghyam.commons.utils

import android.text.TextPaint
import android.text.style.URLSpan

class URLSpanNoUnderline(url: String) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint){
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}