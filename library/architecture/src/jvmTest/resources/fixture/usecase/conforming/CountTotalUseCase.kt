package fixture.cases.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CountTotalUseCase(
    private val first: Flow<Int>,
    private val second: Flow<Int>,
) {
    operator fun invoke(): Flow<Int> =
        combine(first, second) { a, b ->
            a + b
        }

    fun isEmpty(total: Int): Boolean {
        val empty = total == 0
        return empty
    }

    private fun helper() = Unit
}
