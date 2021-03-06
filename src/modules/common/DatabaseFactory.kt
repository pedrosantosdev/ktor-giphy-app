package io.pedro.santos.dev.modules.common

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import io.pedro.santos.dev.modules.user.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbUser = appConfig.property("db.User").getString()
    private val dbPassword = appConfig.property("db.Password").getString()
    private val dbHost = appConfig.property("db.Host").getString()
    private val dbPort = appConfig.property("db.Port").getString()
    private val dbSchema = appConfig.property("db.Schema").getString()
    private val dbUrl = appConfig.propertyOrNull("db.Url")?.getString() ?: "jdbc:postgresql://${dbHost}:${dbPort}/${dbSchema}"

    fun init() {
        Database.connect(dataSource())
        transaction {
            SchemaUtils.create(UsersTable)
        }
    }

    private fun dataSource(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = dbUrl
        config.username = dbUser
        config.password = dbPassword
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T = newSuspendedTransaction { block() }

}