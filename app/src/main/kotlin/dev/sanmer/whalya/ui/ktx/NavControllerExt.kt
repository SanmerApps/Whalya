package dev.sanmer.whalya.ui.ktx

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun <T : Any> NavController.navigateSingleTopTo(
    route: T,
    builder: NavOptionsBuilder.() -> Unit = {}
) = navigate(route) {
    launchSingleTop = true
    restoreState = true
    builder()
}

fun <T : Any> NavController.navigatePopTo(
    route: T
) = navigateSingleTopTo(route) {
    popUpTo(route) {
        inclusive = false
    }
}