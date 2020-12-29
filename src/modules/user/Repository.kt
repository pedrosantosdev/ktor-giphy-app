package io.pedro.santos.dev.modules.user

import io.pedro.santos.dev.modules.common.DatabaseFactory.dbQuery
import io.pedro.santos.dev.modules.common.Repository
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserRepository: Repository<User> {
    override suspend fun findAll(): Iterable<User> =
        dbQuery {
            UsersTable.selectAll().map {
                toUser(it)
            }
        }

    override suspend fun findById(id: Int): User? =
        dbQuery {
            UsersTable.select{
                UsersTable.id eq id
                UsersTable.deletedAt eq null
            }.limit(1).map {
                toUser(it)
            }.firstOrNull()
        }

    suspend fun findByUsername(username: String): User? =
        dbQuery {
            UsersTable.select{
                UsersTable.username eq username
                UsersTable.deletedAt eq null
            }.limit(1).map{
                toUser(it)
            }.firstOrNull()
        }

    override suspend fun create(entity: User): User? {
        val id = dbQuery {
            UsersTable.insertAndGetId{
                it[UsersTable.username] = entity.username
                it[UsersTable.password] = BCryptPasswordEncoder().encode(entity.password)
                it[UsersTable.active] = true
                it[UsersTable.createdAt] = DateTime.now()
            }
        }
        return findById(id.value)
    }

    override suspend fun update(entity: User): User? {
        dbQuery {
            UsersTable.update({ UsersTable.id eq entity.id }, limit = 1) {
                it[UsersTable.username] = entity.username
                it[UsersTable.active] = entity.active
                it[UsersTable.modifiedAt] = DateTime.now()
            }
        }
        return findById(entity.id.value)
    }

    override suspend fun deleteById(id: Int): Int =
        dbQuery {
            UsersTable.update({ UsersTable.id eq id }, limit = 1) {
                it[UsersTable.active] = false
                it[UsersTable.deletedAt] = DateTime.now()
            }
        }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[UsersTable.id],
            username = row[UsersTable.username],
            active = row[UsersTable.active],
            password = row[UsersTable.password]
        )
}