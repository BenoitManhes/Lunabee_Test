package com.example.lunabeeusers.utils

import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.clearSpans
import com.bumptech.glide.Glide

fun ImageView.loadImageFromUrl(imgUrl: String?) {
    Glide.with(context)
        .load(imgUrl)
        .into(this)
}

fun TextView.highlighTerm(wordPrefix: String) {
    // Use lowerCase to match without considering case
    val startIndex: Int = text.toString().toLowerCase().indexOf(wordPrefix.toLowerCase())
    val stopIndex: Int = startIndex + wordPrefix.length
    if (startIndex != -1) {
        val result = SpannableString(text)
        result.setSpan(ForegroundColorSpan(highlightColor), startIndex, stopIndex, 0)
        result.setSpan(StyleSpan(BOLD), startIndex, stopIndex, 0)
        text = result
    }
}

fun TextView.clearHighligh() {
    var result = SpannableString(text)
    result.clearSpans()
    text = result
}