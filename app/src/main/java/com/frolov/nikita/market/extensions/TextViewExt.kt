package com.frolov.nikita.market.extensions

import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.frolov.nikita.market.MarketApp

fun TextView.setTextColorCompat(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(MarketApp.instance, colorRes))
}

fun TextView.getStringText() = this.text.toString()
