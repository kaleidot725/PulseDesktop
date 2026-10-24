package fixture.conforming.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CounterRepository {
    val count: StateFlow<Int>

    fun increment()
}

class InMemoryCounterRepository : CounterRepository {
    private val mutableCount = MutableStateFlow(0)

    override val count: StateFlow<Int> = mutableCount.asStateFlow()

    val name = "counter"

    val label get() = "counter: ${count.value}"

    override fun increment() {
        mutableCount.value += 1
    }
}
