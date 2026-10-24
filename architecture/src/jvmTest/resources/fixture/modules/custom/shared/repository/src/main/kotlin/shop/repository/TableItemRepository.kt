package shop.repository

import shop.db.ItemTable
import shop.entity.Item
import shop.usecase.ItemRepository

class TableItemRepository(private val table: ItemTable) : ItemRepository {
    override fun all(): List<Item> = table.rows()
}
