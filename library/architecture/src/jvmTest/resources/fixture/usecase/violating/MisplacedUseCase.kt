package fixture.cases.domain

class MisplacedUseCase {
    operator fun invoke(values: List<Int>): Int {
        val positive = values.filter { it > 0 }
        return positive.sum()
    }
}
