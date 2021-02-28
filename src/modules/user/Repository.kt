package io.pedro.santos.dev.modules.user

import io.ktor.util.date.*
import io.pedro.santos.dev.BadRequestException
import io.pedro.santos.dev.modules.common.DatabaseFactory.dbQuery
import io.pedro.santos.dev.modules.common.Repository
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.text.SimpleDateFormat
import java.util.*

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
            }.andWhere { UsersTable.deletedAt eq null }.
            limit(1).map {
                toUser(it)
            }.firstOrNull()
        }

    suspend fun findByUsername(username: String): User? =
        dbQuery {
            UsersTable.select{
                UsersTable.username eq username
            }.andWhere { UsersTable.deletedAt eq null }.limit(1).map{
                toUser(it)
            }.firstOrNull()
        }

    override suspend fun create(entity: User): User? {
        if (findByUsername(entity.username) != null) throw BadRequestException("Username already taken")

        val id = dbQuery {
            UsersTable.insertAndGetId{
                it[UsersTable.username] = entity.username
                it[UsersTable.password] = BCryptPasswordEncoder().encode(entity.password)
                it[UsersTable.active] = entity.active
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
        if (!entity.password.isNullOrEmpty()) changePassword(entity.id, entity.password)
        return findById(entity.id)
    }

    private suspend fun changePassword(id: Int, password: String) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id }, limit = 1) {
                it[UsersTable.password] = BCryptPasswordEncoder().encode(password)
                it[UsersTable.modifiedAt] = DateTime.now()
            }
        }
    }

    override suspend fun deleteById(id: Int): Int =
        dbQuery {
            UsersTable.update({ UsersTable.id eq id }, limit = 1) {
                it[UsersTable.active] = false
                it[UsersTable.deletedAt] = DateTime.now()
            }
        }

    suspend fun  deleteDeactivatedUser(days: Int = 30): Int {
        val diff = Calendar.getInstance().add(Calendar.DAY_OF_YEAR, -days)
        return dbQuery {
            UsersTable.deleteWhere { UsersTable.modifiedAt lessEq (SimpleDateFormat("yyyy-MM-dd").format(diff)) }
        }
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[UsersTable.id].value,
            username = row[UsersTable.username],
            active = row[UsersTable.active],
            password = row[UsersTable.password]
        )
}