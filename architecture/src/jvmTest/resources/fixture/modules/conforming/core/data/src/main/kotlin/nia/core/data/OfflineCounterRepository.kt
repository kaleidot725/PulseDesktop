package nia.core.data

import nia.core.database.CounterDao
import nia.core.domain.CounterRepository
import nia.core.model.Counter

class OfflineCounterRepository(private val dao: CounterDao) : CounterRepository {
    override fun load(): Counter = dao.read()
}
