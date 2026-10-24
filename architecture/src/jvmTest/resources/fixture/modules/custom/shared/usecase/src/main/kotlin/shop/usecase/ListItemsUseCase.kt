package shop.usecase

import shop.entity.Item

class ListItemsUseCase(private val repository: ItemRepository) {
    operator fun invoke(): List<Item> = repository.all()
}
