package counter.core.domain

import counter.core.data.repository.InMemoryCounterRepository

class DomainKnowsTheData(val repository: InMemoryCounterRepository)
