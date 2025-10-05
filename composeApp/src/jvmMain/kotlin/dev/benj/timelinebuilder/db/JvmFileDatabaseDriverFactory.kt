package dev.benj.timelinebuilder.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.benj.timelinebuilder.tlevent.TimelineEventDatabase
import java.util.Properties


class JvmFileDatabaseDriverFactory(): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {


        val driver = JdbcSqliteDriver("jdbc:sqlite:file:data.db", Properties().apply { setProperty("date_class", "TEXT") }, TimelineEventDatabase.Schema)

        return driver
    }
}