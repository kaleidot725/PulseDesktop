package jp.kaleidot725.pulse.mvi

internal data class ContentState(
    val count: Int = 0,
) : PulseState

internal sealed class ContentAction : PulseAction {
    data object Increment : ContentAction()
}

internal sealed class ContentEvent : PulseEvent {
    data class Ping(
        val text: String,
    ) : ContentEvent()
}

internal class ContentViewModel : PulseViewModel<ContentState, ContentAction, ContentEvent>(ContentState()) {
    var setupCount = 0

    override fun onSetup() {
        setupCount++
    }

    override fun onAction(uiAction: ContentAction) {
        when (uiAction) {
            ContentAction.Increment -> update { copy(count = count + 1) }
        }
    }
}
