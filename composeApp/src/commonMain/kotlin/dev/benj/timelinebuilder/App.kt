package dev.benj.timelinebuilder

import androidx.compose.runtime.*
import dev.benj.timelinebuilder.tlevent.TLEventViewModel
import dev.benj.timelinebuilder.tlevent.tlEventModule
import dev.benj.timelinebuilder.ui.theme.TimelineBuilderTheme
import dev.benj.timelinebuilder.ui.TimelineDisplay
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin


@Composable
@Preview
fun App()  {
    TimelineBuilderTheme {
        TimelineDisplay(
            viewModel = koinViewModel<TLEventViewModel>(),
        )
    }
}