package counter.core.domain

import counter.core.data.InMemoryCounterRepository

class DomainKnowsTheData(val repository: InMemoryCounterRepository)
