package com.sagaRock101.playmusic.ui.interfaces

import com.sagaRock101.playmusic.model.Song

interface OnSongItemClickedListener {
    fun startPlayer(song: Song, position: Int)
}