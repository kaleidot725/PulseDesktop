package shop.domain.repository

import shop.domain.model.Item

interface ItemRepository {
    fun all(): List<Item>
}
