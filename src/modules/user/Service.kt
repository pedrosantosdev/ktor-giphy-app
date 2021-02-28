package io.pedro.santos.dev.modules.user

class UserService {
    suspend fun create(entity: User): User? {
        return UserRepository().create(entity)
    }

    suspend fun findAll(): Iterable<User> {
        return UserRepository().findAll()
    }

    suspend fun findById(id: Int): User? {
        return UserRepository().findById(id)
    }

    suspend fun update(entity: User): User? {
        return UserRepository().update(entity)
    }

    suspend fun deleteById(id: Int): Int {
        return UserRepository().deleteById(id)
    }

    suspend fun deleteDeactivated(): Int {
        return UserRepository().deleteDeactivatedUser()
    }
}