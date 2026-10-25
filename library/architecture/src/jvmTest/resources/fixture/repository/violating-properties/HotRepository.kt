package shop.repository

import kotlinx.coroutines.flow.SharedFlow

interface HotRepository {
    val events: SharedFlow<Int>
}
