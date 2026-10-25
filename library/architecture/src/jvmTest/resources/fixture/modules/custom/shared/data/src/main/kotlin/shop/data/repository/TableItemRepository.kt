package shop.data.repository

import shop.domain.model.Item
import shop.domain.repository.ItemRepository

class TableItemRepository : ItemRepository {
    override fun all(): List<Item> = emptyList()
}
