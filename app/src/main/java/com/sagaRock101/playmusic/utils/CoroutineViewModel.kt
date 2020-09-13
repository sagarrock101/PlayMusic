package com.sagaRock101.playmusic.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class CoroutineViewModel(
    private val mainDispatcher: CoroutineDispatcher
): ViewModel() {
    private val job = Job()
    val scope = CoroutineScope(mainDispatcher + job)

    fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}