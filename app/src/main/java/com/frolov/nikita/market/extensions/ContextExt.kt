package com.frolov.nikita.market.extensions

import android.content.Context
import android.support.v4.content.ContextCompat

fun Context.getInteger(intRes: Int) = this.resources.getInteger(intRes)

fun Context.getContextCompatColor(colorRes: Int) = ContextCompat.getColor(this, colorRes)