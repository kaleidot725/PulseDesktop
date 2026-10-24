package shop.data

import shop.domain.Item
import shop.domain.ItemRepository

class TableItemRepository : ItemRepository {
    override fun all(): List<Item> = emptyList()
}
