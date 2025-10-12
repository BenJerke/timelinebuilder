package dev.benj.timelinebuilder

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.benj.timelinebuilder.tlevent.tlEventModule
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "timelinebuilder",
    ) {
        startKoin { modules(tlEventModule, jvmPlatformModule) }
        App()
    }
}