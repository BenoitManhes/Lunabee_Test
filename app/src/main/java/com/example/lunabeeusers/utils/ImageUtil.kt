package com.example.lunabeeusers.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageFromUrl(imgUrl: String?) {
    Glide.with(context)
        .load(imgUrl)
        .into(this)
}