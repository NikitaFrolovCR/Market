package com.frolov.nikita.market.extensions

import android.os.Bundle
import android.os.SystemClock

fun Bundle.restfulClick(delay: Int = 300, block: () -> Unit) =
        SystemClock.elapsedRealtime().minus(getLong("restfulClick", 0)).takeIf { it >= delay }?.let {
            takeIf({ !it.isEmpty }) { Bundle() }.apply { putLong("restfulClick", SystemClock.elapsedRealtime()); block() }
        } ?: this

