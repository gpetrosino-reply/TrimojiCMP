package it.reply.open.trimoji.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class TrimojiViewModel<UI, EFF>: ViewModel() {
    private val effects: MutableSharedFlow<EFF> = MutableSharedFlow()

    abstract val uiState: StateFlow<UI>


    protected fun produceEffect(effect: EFF) = launchInViewModelScope {
        effects.emit(effect)
    }

    protected val scope by lazy {
        viewModelScope + Dispatchers.Default //TODO exception handler
    }

    protected fun launchInViewModelScope(
        block: suspend CoroutineScope.() -> Unit
    ): Job = (scope).launch(
        block = block
    )

    protected fun <T> Flow<T>.stateInViewModelScope(
        initialValue: T,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000L),
    ) = stateIn(
        scope = scope,
        started = started,
        initialValue = initialValue
    )

    protected fun <T> Flow<T>.shareInViewModelScope(
        started: SharingStarted = SharingStarted.WhileSubscribed(5000L),
    ) = shareIn(
        scope = scope,
        started = started,
    )

    protected fun <T> Flow<T>.launchInViewModelScope() = launchIn(scope)

}

