package shop.repository

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

sealed interface CartError {
    data object NotFound : CartError
}

interface CatalogRepository {
    val names: Flow<List<String>>
}

interface CartRepository {
    val items: StateFlow<List<String>>

    fun total(): Flow<Int>

    suspend fun find(id: String): Result<String, CartError>

    suspend fun add(id: String)

    suspend fun clearAll(): Unit
}

class InMemoryCartRepository(
    val catalog: CatalogRepository,
    private val readPrices: suspend () -> List<Int>,
    val priceOf: (String) -> Int,
) : CartRepository {
    private val state = MutableStateFlow(emptyList<String>())

    override val items: StateFlow<List<String>> = state

    override fun total(): Flow<Int> = flow { emit(readPrices().sum()) }

    override suspend fun find(id: String): Result<String, CartError> = Ok(id)

    override suspend fun add(id: String) {
        state.value += id
    }

    override suspend fun clearAll(): Unit = clear()

    fun clear() {
        state.value = emptyList()
    }

    private fun count(): Int = state.value.size
}
