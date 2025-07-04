package dev.sanmer.whalya.ui.screens.servers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.whalya.R
import dev.sanmer.whalya.database.entity.ServerEntity
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.ui.component.ActivePoint
import dev.sanmer.whalya.ui.component.DeadPoint
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.PageIndicator
import dev.sanmer.whalya.ui.component.PageIndicatorDefaults
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.WithIcon
import dev.sanmer.whalya.ui.ktx.navigateSingleTopTo
import dev.sanmer.whalya.ui.ktx.plus
import dev.sanmer.whalya.ui.main.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ServersScreen(
    viewModel: ServersViewModel = koinViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                isNetworkAvailable = viewModel.isNetworkAvailable,
                onClick = { navController.navigateSingleTopTo(Screen.AddServer()) }
            )
        }
    ) { contentPadding ->
        when (val data = viewModel.data) {
            is LoadData.Success<List<ServerEntity>> -> if (data.value.isNotEmpty()) {
                ServersContent(
                    navController = navController,
                    servers = data.value,
                    onSetServer = { viewModel.server = it },
                    onPing = viewModel::ping,
                    contentPadding = contentPadding,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                )
            } else {
                PageIndicator(
                    icon = {
                        PageIndicatorDefaults.Icon(
                            painter = painterResource(R.drawable.cloud_off),
                            contentDescription = null
                        )
                    }
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun ServersContent(
    navController: NavController,
    servers: List<ServerEntity>,
    onSetServer: (ServerEntity) -> Unit,
    onPing: (ServerEntity) -> Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = contentPadding + PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(servers) { server ->
            ServerItem(
                server = server,
                onPing = onPing
            ) {
                onSetServer(server)
                navController.navigateSingleTopTo(Screen.Home(server.id, server.name))
            }
        }
    }
}

@Composable
private fun ServerItem(
    server: ServerEntity,
    onPing: (ServerEntity) -> Boolean,
    onClick: () -> Unit
) {
    val pinged by remember(server.id) {
        derivedStateOf { onPing(server) }
    }

    ValuesColumn(
        onClick = {
            if (pinged) {
                onClick()
            } else {
                onPing(server)
            }
        },
        enabled = true
    ) {
        WithIcon(
            icon = {
                if (pinged) {
                    ActivePoint(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    DeadPoint(
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        ) {
            ValueText(
                title = server.name,
                value = server.baseUrl,
                modifier = Modifier.weight(1f)
            )
        }

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LabelText(
                text = server.version
            )

            LabelText(
                text = server.os
            )

            LabelText(
                text = server.arch
            )
        }

    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.server_title)) },
        actions = {
            IconButton(
                onClick = { navController.navigateSingleTopTo(Screen.Settings) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings_2),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ActionButton(
    isNetworkAvailable: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { if (isNetworkAvailable) onClick() },
        containerColor = if (isNetworkAvailable)
            FloatingActionButtonDefaults.containerColor
        else
            MaterialTheme.colorScheme.errorContainer
    ) {
        Icon(
            painter = painterResource(
                if (isNetworkAvailable)
                    R.drawable.cloud_plus
                else
                    R.drawable.cloud_off
            ),
            contentDescription = null
        )
    }
}