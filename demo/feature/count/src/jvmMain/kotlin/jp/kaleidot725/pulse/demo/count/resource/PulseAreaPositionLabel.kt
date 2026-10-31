package jp.kaleidot725.pulse.demo.count.resource

import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.demo.count.resources.Res
import jp.kaleidot725.pulse.demo.count.resources.position_bottom_left
import jp.kaleidot725.pulse.demo.count.resources.position_bottom_right
import jp.kaleidot725.pulse.demo.count.resources.position_top_left
import jp.kaleidot725.pulse.demo.count.resources.position_top_right
import org.jetbrains.compose.resources.StringResource

/** The domain names a position by its type; the words a screen shows for it are this feature's. */
internal val PulseAreaPosition.label: StringResource
    get() =
        when (this) {
            PulseAreaPosition.TopLeft -> Res.string.position_top_left
            PulseAreaPosition.TopRight -> Res.string.position_top_right
            PulseAreaPosition.BottomLeft -> Res.string.position_bottom_left
            PulseAreaPosition.BottomRight -> Res.string.position_bottom_right
        }
