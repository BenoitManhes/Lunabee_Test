package com.example.lunabeeusers.utils

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
    val startIndex: Int = text.indexOf(wordPrefix)
    val stopIndex: Int = startIndex + wordPrefix.length
    if (startIndex != -1) {
        val result = SpannableString(text)
        result.setSpan(ForegroundColorSpan(highlightColor), startIndex, stopIndex + wordPrefix.length, 0)
        text = result
    }
}

fun TextView.clearHighligh() {
    var result = SpannableString(text)
    result.clearSpans()
    text = result
}