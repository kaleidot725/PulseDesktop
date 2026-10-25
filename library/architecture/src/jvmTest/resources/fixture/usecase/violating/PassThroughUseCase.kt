package fixture.cases.domain.usecase

import kotlinx.coroutines.flow.Flow

class PassThroughUseCase(
    private val counts: Flow<Int>,
) {
    operator fun invoke(): Flow<Int> = counts
}

class BlockPassThroughUseCase(
    private val counts: Flow<Int>,
) {
    operator fun invoke(): Flow<Int> {
        return counts
    }
}
