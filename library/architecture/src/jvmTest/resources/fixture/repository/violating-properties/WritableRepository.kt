package shop.repository

import kotlinx.coroutines.flow.MutableStateFlow

class WritableRepository {
    val items: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
}
