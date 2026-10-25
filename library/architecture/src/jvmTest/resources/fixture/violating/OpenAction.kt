package fixture.violating

import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseBroadcast
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseUnicast

interface OpenAction : PulseAction

sealed interface InterfaceEvent : PulseEvent

open class OpenBroadcast : PulseBroadcast

object LoneUnicast : PulseUnicast
