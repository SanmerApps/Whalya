package dev.sanmer.docker.ui.screens.settings

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.core.Docker
import dev.sanmer.docker.BuildConfig
import dev.sanmer.docker.Const
import dev.sanmer.docker.R
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.component.ValuesColumn
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.viewmodel.SettingsViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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
                title = stringResource(R.string.setting_build_info)
            ) {
                BuildInfoCard()
            }
        }
    }
}

@Composable
private fun BuildInfoCard() {
    val buildTime = rememberSaveable {
        Instant.fromEpochMilliseconds(BuildConfig.BUILD_TIME)
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