package io.pedro.santos.dev.modules.user

import org.jetbrains.exposed.dao.IntIdTable

data class User(val id: Int, val username: String, val password: String)

object UsersTable: IntIdTable("users") {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)

    val createdAt = datetime("created_at")
    val modifiedAt = datetime("modified_at").nullable()
    val deletedAt = datetime("deleted_at").nullable()
}
