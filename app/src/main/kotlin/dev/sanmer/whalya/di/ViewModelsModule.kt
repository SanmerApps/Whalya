package dev.sanmer.whalya.di

import dev.sanmer.whalya.ui.screens.home.HomeViewModel
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerLogsViewModel
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerStatsViewModel
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerViewModel
import dev.sanmer.whalya.ui.screens.inspect.image.ImageViewModel
import dev.sanmer.whalya.ui.screens.inspect.network.NetworkViewModel
import dev.sanmer.whalya.ui.screens.inspect.volume.VolumeViewModel
import dev.sanmer.whalya.ui.screens.licenses.LicensesViewModel
import dev.sanmer.whalya.ui.screens.servers.AddServerViewModel
import dev.sanmer.whalya.ui.screens.servers.ServersViewModel
import dev.sanmer.whalya.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModels = module {
    viewModelOf(::ServersViewModel)
    viewModelOf(::AddServerViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::LicensesViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ContainerViewModel)
    viewModelOf(::ContainerStatsViewModel)
    viewModelOf(::ContainerLogsViewModel)
    viewModelOf(::ImageViewModel)
    viewModelOf(::NetworkViewModel)
    viewModelOf(::VolumeViewModel)
}