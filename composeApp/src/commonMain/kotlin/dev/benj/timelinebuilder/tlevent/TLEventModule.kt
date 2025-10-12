package dev.benj.timelinebuilder.tlevent
import dev.benj.timelinebuilder.db.DatabaseDriverFactory
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
/**
 * Koin module declaration for TL Event-related dependencies
 *
 */
val tlEventModule = module {
    singleOf(::SqliteTLEventRepository) { bind<TLEventRepository>() }
    viewModelOf(::TLEventViewModel)
}