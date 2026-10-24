package nia.core.ui

import androidx.compose.runtime.Composable
import nia.core.designsystem.PulseButton
import nia.core.model.Counter

@Composable
fun CounterCard(counter: Counter) = PulseButton(counter.value.toString())
