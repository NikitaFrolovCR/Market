package com.frolov.nikita.market.extensions

import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifDrawable.LOOP_FOREVER
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.ImageViewTarget

fun ImageView.loadImage(path: String) = Glide.with(context).load(path).into(this)

fun ImageView.loadImage(@DrawableRes resourcesId: Int) = Glide.with(context).load(resourcesId).into(this)

fun ImageView.loadImageCircleCrop(path: String, @DrawableRes placeholderId: Int) =
        Glide.with(context).load(path).apply(bitmapTransform(CircleCrop()).placeholder(placeholderId)).into(this)

fun ImageView.loadGif(path: String) = Glide.with(context).asGif().load(path).into(this)

fun ImageView.loadGif(@DrawableRes resourceId: Int, loopCount: Int = LOOP_FOREVER) = Glide.with(context).asGif().load(resourceId)
        .into(object : ImageViewTarget<GifDrawable>(this) {
            override fun setResource(resource: GifDrawable?) {
                resource?.setLoopCount(loopCount)
                view.setImageDrawable(resource)
            }
        })

fun ImageView.loadBase64(base64: String) = Base64.decode(base64, Base64.DEFAULT)
        .also { setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size)) }