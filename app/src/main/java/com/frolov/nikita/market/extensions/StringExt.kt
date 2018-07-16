package com.frolov.nikita.market.extensions

import android.text.Html

fun String.removeSpaces() = this.replace(" ", "")

fun String.removeSlash() = this.replace("/".toRegex(), "")

fun String.substringBefore(endIndex: Int) = this.substring(0, endIndex)

private val tagHandler = Html.TagHandler { opening, tag, output, _ ->
    when (tag) {
        "ul" -> if (!opening) output.append("\n")
        "li" -> if (opening) output.append("\n\t•")
    }
}

fun String.isOneSign() = length == 1