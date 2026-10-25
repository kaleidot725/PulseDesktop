package fixture.violating

import jp.kaleidot725.pulse.mvi.PulseAction

sealed class CaseAction : PulseAction {
    object PlainCase : CaseAction()

    class OpenCase(
        val amount: Int,
    ) : CaseAction()

    sealed class Group : CaseAction() {
        class NestedCase : Group()
    }
}
