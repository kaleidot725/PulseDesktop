package nia.core.database

import nia.core.model.Counter

class CounterDao {
    fun read(): Counter = Counter(0)
}
