package dev.sanmer.whalya.ui.main

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sanmer.whalya.ui.screens.home.HomeScreen
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerLogsScreen
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerScreen
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerStatsScreen
import dev.sanmer.whalya.ui.screens.inspect.image.ImageScreen
import dev.sanmer.whalya.ui.screens.inspect.network.NetworkScreen
import dev.sanmer.whalya.ui.screens.inspect.volume.VolumeScreen
import dev.sanmer.whalya.ui.screens.licenses.LicensesScreen
import dev.sanmer.whalya.ui.screens.servers.AddServerScreen
import dev.sanmer.whalya.ui.screens.servers.ServersScreen
import dev.sanmer.whalya.ui.screens.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.background
        ),
        navController = navController,
        startDestination = Screen.Servers
    ) {
        composable<Screen.Servers> {
            ServersScreen(
                navController = navController
            )
        }

        composable<Screen.AddServer> {
            AddServerScreen(
                navController = navController
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                navController = navController
            )
        }

        composable<Screen.Licenses> {
            LicensesScreen(
                navController = navController
            )
        }

        composable<Screen.Home> {
            HomeScreen(
                navController = navController
            )
        }

        composable<Screen.Container> {
            ContainerScreen(
                navController = navController
            )
        }

        composable<Screen.ContainerStats> {
            ContainerStatsScreen(
                navController = navController
            )
        }

        composable<Screen.ContainerLogs> {
            ContainerLogsScreen(
                navController = navController
            )
        }

        composable<Screen.Image> {
            ImageScreen(
                navController = navController
            )
        }

        composable<Screen.Network> {
            NetworkScreen(
                navController = navController
            )
        }

        composable<Screen.Volume> {
            VolumeScreen(
                navController = navController
            )
        }
    }
}

sealed interface Screen {
    @Serializable
    data object Servers : Screen

    @Serializable
    data class AddServer(
        val id: Long = -1L
    ) : Screen {
        val isEdit = id != -1L
    }

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Licenses : Screen

    @Serializable
    data class Home(val id: Long, val name: String) : Screen

    @Serializable
    data class Container(val id: String) : Screen

    @Serializable
    data class ContainerStats(val id: String) : Screen

    @Serializable
    data class ContainerLogs(val id: String) : Screen

    @Serializable
    data class Image(val id: String) : Screen

    @Serializable
    data class Network(val id: String) : Screen

    @Serializable
    data class Volume(val name: String) : Screen
}