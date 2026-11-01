package fixture.conforming.counter.content.total

import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState

data class TotalState(
    val count: Int = 0,
) : PulseState

sealed class TotalAction : PulseAction {
    data object Increment : TotalAction()

    data class Add(
        val amount: Int,
    ) : TotalAction()

    sealed class Edit : TotalAction() {
        data object Undo : Edit()
    }
}

sealed class TotalEvent : PulseEvent {
    data class Ping(
        val text: String,
    ) : TotalEvent()
}

data class Pinged(
    val text: String,
) : PulseEvent

data object EmptyState : PulseState
