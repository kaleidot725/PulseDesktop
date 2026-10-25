package shop.repository

interface StdlibResultRepository {
    suspend fun load(): Result<Int>
}
