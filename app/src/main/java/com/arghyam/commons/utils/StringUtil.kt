package com.arghyam.commons.utils

import android.text.Spannable
import android.text.style.URLSpan
import android.util.Log

class StringUtil {
    fun removeUnderlines(p_Text: Spannable) {
        Log.e("Anirudh remove", p_Text.toString())
        val spans = p_Text.getSpans(0, p_Text.length, URLSpan::class.java)
        Log.e("Anirudh spans",spans.toString())

        var email: URLSpanNoUnderline
        for (span in spans) {
            val start = p_Text.getSpanStart(span)
            val end = p_Text.getSpanEnd(span)
            p_Text.removeSpan(span)
            Log.e("Anirudh span",span.toString())

            email = URLSpanNoUnderline(span.url)
            p_Text.setSpan(email, start, end, 0)
        }
    }
}