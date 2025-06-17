package dev.sanmer.docker.ui.screens.servers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.docker.R
import dev.sanmer.docker.database.entity.ServerEntity
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.ui.component.ActivePoint
import dev.sanmer.docker.ui.component.DeadPoint
import dev.sanmer.docker.ui.component.LabelText
import dev.sanmer.docker.ui.component.PageIndicator
import dev.sanmer.docker.ui.component.PageIndicatorDefaults
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.component.ValuesColumn
import dev.sanmer.docker.ui.component.WithIcon
import dev.sanmer.docker.ui.ktx.navigateSingleTopTo
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.ui.main.Screen
import dev.sanmer.docker.viewmodel.ServersViewModel

@Composable
fun ServersScreen(
    viewModel: ServersViewModel = hiltViewModel(),
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
            ActionButton { navController.navigateSingleTopTo(Screen.AddServer()) }
        }
    ) { contentPadding ->
        when (val data = viewModel.data) {
            is LoadData.Success<List<ServerEntity>> -> if (data.value.isNotEmpty()) {
                ServersContent(
                    navController = navController,
                    servers = data.value,
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
                navController.navigateSingleTopTo(Screen.Home(server.id))
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
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.cloud_plus),
            contentDescription = null
        )
    }
}