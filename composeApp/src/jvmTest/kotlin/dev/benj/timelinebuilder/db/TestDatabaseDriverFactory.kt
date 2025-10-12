package dev.benj.timelinebuilder.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.benj.timelinebuilder.tlevent.TimelineEventDatabase
import java.util.Properties

class TestDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        // Use in-memory database for tests
        val driver = JdbcSqliteDriver(
            JdbcSqliteDriver.IN_MEMORY,
            Properties().apply { setProperty("date_class", "TEXT") }
        )

        // Create the schema
        TimelineEventDatabase.Schema.create(driver)

        return driver
    }
}

