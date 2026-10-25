package shop.domain

class ListItemsUseCase(private val repository: ItemRepository) {
    operator fun invoke(): List<Item> = repository.all()
}
