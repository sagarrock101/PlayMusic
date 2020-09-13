package com.sagaRock101.playmusic.utils

import android.database.Cursor

fun Cursor?.forEach(
    each: Cursor?.() -> Unit
) {
    this ?: return
    if (moveToFirst()) {
        do {
            each(this)
        } while (moveToNext())
    }
}

fun <T> Cursor?.toList(
    mapper: Cursor?.() -> T
): MutableList<T> {
    var list = mutableListOf<T>()
    forEach {
        list.add(mapper(this))
    }
    return list
}