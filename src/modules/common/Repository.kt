package io.pedro.santos.dev.modules.common

interface Repository<T> {
    suspend fun findAll(): Iterable<T>?
    suspend fun findById(id: Int): T?
    suspend fun deleteById(id: Int): Int
    suspend fun update(entity: T): T?
    suspend fun create(entity: T): T?
}