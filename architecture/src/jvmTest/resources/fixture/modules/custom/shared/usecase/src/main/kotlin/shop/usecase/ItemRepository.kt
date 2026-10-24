package shop.usecase

import shop.entity.Item

interface ItemRepository {
    fun all(): List<Item>
}
