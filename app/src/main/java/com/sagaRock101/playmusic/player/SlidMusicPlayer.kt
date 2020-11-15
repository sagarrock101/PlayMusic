package com.sagaRock101.playmusic.player

interface SlidMusicPlayer {
    fun onPlay()
    fun onPause()
    fun setSource(path: String)
}

