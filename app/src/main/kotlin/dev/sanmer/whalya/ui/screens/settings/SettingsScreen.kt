package dev.sanmer.whalya.ui.screens.settings

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.core.Docker
import dev.sanmer.whalya.BuildConfig
import dev.sanmer.whalya.Const
import dev.sanmer.whalya.R
import dev.sanmer.whalya.ktx.toLocalDateTime
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.ktx.navigateSingleTopTo
import dev.sanmer.whalya.ui.ktx.plus
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.datetime.TimeZone
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .animateContentSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding + PaddingValues(all = 20.dp)),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TitleColum(
                title = stringResource(R.string.server_title)
            ) {
                ServersContent(
                    navController = navController,
                    servers = viewModel.servers
                )
            }

            TitleColum(
                title = stringResource(R.string.setting_about)
            ) {
                BuildInfoCard(
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun BuildInfoCard(
    navController: NavController,
) {
    val buildTime = rememberSaveable {
        BuildConfig.BUILD_TIME
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()
    }

    val version = rememberSaveable {
        "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    ValuesColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ValueText(
            title = stringResource(R.string.docker_api),
            value = Docker.API_VERSION,
            modifier = Modifier.padding(all = 15.dp)
        )

        HorizontalDivider()

        ValueText(
            title = stringResource(R.string.setting_version),
            value = version,
            modifier = Modifier.padding(all = 15.dp)
        )

        HorizontalDivider()

        ValueText(
            title = stringResource(R.string.docker_build_time),
            value = buildTime,
            modifier = Modifier.padding(all = 15.dp)
        )

        HorizontalDivider()

        ValueText(
            title = stringResource(R.string.setting_licenses),
            value = stringResource(R.string.setting_licenses_desc),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigateSingleTopTo(Screen.Licenses) }
                .padding(all = 15.dp)
        )
    }
}

@Composable
private fun TitleColum(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 15.dp)
        )

        content()
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text(text = stringResource(R.string.setting_title)) },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    context.startActivity(
                        Intent.parseUri(Const.GITHUB_URL, Intent.URI_INTENT_SCHEME)
                    )
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.brand_github),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}