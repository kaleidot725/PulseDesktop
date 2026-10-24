package fixture.violating.repository

import kotlinx.coroutines.flow.MutableStateFlow

class LeakyRepository {
    val count: MutableStateFlow<Int> = MutableStateFlow(0)
}

class ListRepository {
    val history = mutableListOf<Long>()
}

class WritableRepository {
    var lastChanged: Long = 0
}
