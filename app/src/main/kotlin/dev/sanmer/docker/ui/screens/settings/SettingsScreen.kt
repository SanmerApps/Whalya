package dev.sanmer.docker.ui.screens.settings

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.docker.Const
import dev.sanmer.docker.R
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.viewmodel.SettingsViewModel

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
                .padding(contentPadding + PaddingValues(all = 20.dp))
        ) {
            Text(
                text = stringResource(R.string.server_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 15.dp)
            )

            ServersContent(
                navController = navController,
                servers = viewModel.servers
            )
        }
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