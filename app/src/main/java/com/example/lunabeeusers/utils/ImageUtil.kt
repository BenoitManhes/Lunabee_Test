package com.example.lunabeeusers.utils

import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide

fun loadImageFromUrl(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}