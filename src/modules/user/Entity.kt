package io.pedro.santos.dev.modules.user

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.joda.time.DateTime
import io.ktor.auth.Principal

data class User(val id: Int = 0, val username: String, @Transient val password: String?, val active: Boolean = false): Principal

object UsersTable: IntIdTable("users") {
    val username: Column<String> = varchar("username", 255).uniqueIndex()
    val password: Column<String> = varchar("password", 255)
    val active: Column<Boolean> = bool("active")

    val createdAt: Column<DateTime> = datetime("created_at")
    val modifiedAt: Column<DateTime?> = datetime("modified_at").nullable()
    val deletedAt: Column<DateTime?> = datetime("deleted_at").nullable()
}
