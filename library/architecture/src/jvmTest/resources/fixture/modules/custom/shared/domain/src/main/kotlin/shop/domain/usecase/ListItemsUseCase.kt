package shop.domain.usecase

import shop.domain.model.Item
import shop.domain.repository.ItemRepository

class ListItemsUseCase(private val repository: ItemRepository) {
    operator fun invoke(): List<Item> =
        repository
            .all()
            .filter { it.name.isNotBlank() }
}
