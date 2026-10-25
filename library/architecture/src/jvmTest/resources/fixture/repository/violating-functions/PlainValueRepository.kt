package shop.repository

interface PlainValueRepository {
    suspend fun load(): Int
}
