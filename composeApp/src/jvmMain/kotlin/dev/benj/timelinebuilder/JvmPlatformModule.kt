package dev.benj.timelinebuilder
import dev.benj.timelinebuilder.db.DatabaseDriverFactory
import dev.benj.timelinebuilder.db.JvmFileDatabaseDriverFactory
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val jvmPlatformModule = module {
        singleOf(::JvmFileDatabaseDriverFactory) { bind<DatabaseDriverFactory>() }
    }
