package nia.core.domain

import nia.core.model.Counter

interface CounterRepository {
    fun load(): Counter
}
