package counter.core.data.local

import counter.core.data.model.CounterRecord

class CounterDataSource {
    fun read(): CounterRecord = CounterRecord(0)
}
