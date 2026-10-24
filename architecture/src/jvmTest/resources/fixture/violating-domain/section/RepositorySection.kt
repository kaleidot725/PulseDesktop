package fixture.violating.section

import androidx.compose.runtime.Composable
import fixture.violating.repository.LeakyRepository

@Composable
fun RepositorySection(repository: LeakyRepository) {
    println(repository.count.value)
}
