package com.sagaRock101.playmusic.utils

fun String.toMediaId(): String {
    val parts = split("|")
    return if (parts.size > 1)
        parts[1]
    else ""
}