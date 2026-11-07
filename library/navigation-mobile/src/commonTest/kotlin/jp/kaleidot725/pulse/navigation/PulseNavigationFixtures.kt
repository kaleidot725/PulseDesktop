package jp.kaleidot725.pulse.navigation

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState
import jp.kaleidot725.pulse.mvi.PulseViewModel

internal class TestOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
}

internal data class NavState(
    val value: Int = 0,
) : PulseState

internal data object NavAction : PulseAction

internal data object NavEvent : PulseEvent

internal class NavViewModel : PulseViewModel<NavState, NavAction, NavEvent>(NavState()) {
    override fun onAction(uiAction: NavAction) = Unit
}
