package shop.domain

interface ItemRepository {
    fun all(): List<Item>
}
