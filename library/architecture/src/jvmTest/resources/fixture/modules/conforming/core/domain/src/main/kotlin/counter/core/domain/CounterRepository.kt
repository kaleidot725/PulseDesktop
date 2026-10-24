package counter.core.domain

interface CounterRepository {
    fun load(): Counter
}
