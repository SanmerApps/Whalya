package dev.sanmer.whalya.ui.screens.inspect.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.core.response.container.Container
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.ui.inspect.UiContainer
import dev.sanmer.whalya.ui.component.ActivePoint
import dev.sanmer.whalya.ui.component.AnimatedText
import dev.sanmer.whalya.ui.component.DeadPoint
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.OperationButton
import dev.sanmer.whalya.ui.component.OperationResultBottomSheet
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.ValuesFlow
import dev.sanmer.whalya.ui.component.ValuesFlowRow
import dev.sanmer.whalya.ui.component.WithIcon
import dev.sanmer.whalya.ui.ktx.bottom
import dev.sanmer.whalya.ui.ktx.navigatePopTo
import dev.sanmer.whalya.ui.ktx.navigateSingleTopTo
import dev.sanmer.whalya.ui.ktx.plus
import dev.sanmer.whalya.ui.main.Screen
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerViewModel.BottomSheet
import dev.sanmer.whalya.ui.screens.inspect.container.ContainerViewModel.Operate
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContainerScreen(
    viewModel: ContainerViewModel = koinViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(viewModel.result) {
        when (val result = viewModel.result) {
            is LoadData.Success<Operate> -> {
                if (result.value.isDestroyed) {
                    viewModel.update(BottomSheet.Closed)
                    navController.navigateUp()
                }
            }

            else -> {}
        }
        onDispose {}
    }

    when (viewModel.bottomSheet) {
        BottomSheet.Closed -> {}
        BottomSheet.Operate -> OperationBottomSheet(
            onDismiss = { viewModel.update(BottomSheet.Closed) },
            onOperate = viewModel::operate,
            state = viewModel.state
        )

        BottomSheet.Result -> OperationResultBottomSheet(
            onDismiss = { viewModel.update(BottomSheet.Operate) },
            data = viewModel.result
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                onRefresh = viewModel::loadData,
                name = viewModel.name,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        when (val data = viewModel.data) {
            LoadData.Pending, LoadData.Loading -> Loading(
                modifier = Modifier.padding(contentPadding)
            )

            is LoadData.Success<UiContainer> -> ContainerContent(
                navController = navController,
                container = data.value,
                onOperate = { viewModel.update(BottomSheet.Operate) },
                contentPadding = contentPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )

            is LoadData.Failure -> Failed(
                error = data.error,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
private fun ContainerContent(
    navController: NavController,
    container: UiContainer,
    onOperate: () -> Unit,
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
        item {
            ContainerCard(
                container = container,
                onStats = {
                    navController.navigateSingleTopTo(Screen.ContainerStats(container.id))
                },
                onLogs = {
                    navController.navigateSingleTopTo(Screen.ContainerLogs(container.id))
                },
                onOperate = onOperate
            )
        }

        item {
            ImageCard(
                container = container
            ) {
                navController.navigatePopTo(Screen.Image(container.imageId))
            }
        }

        items(container.endpoints) { endpoint ->
            NetworkItem(
                endpoint = endpoint
            ) {
                navController.navigatePopTo(Screen.Network(endpoint.id))
            }
        }

        items(container.volumes) { mount ->
            VolumeItem(
                mount = mount
            ) {
                navController.navigatePopTo(Screen.Volume(mount.original.name))
            }
        }
    }
}

@Composable
private fun ContainerCard(
    container: UiContainer,
    onStats: () -> Unit,
    onLogs: () -> Unit,
    onOperate: () -> Unit
) {
    ValuesColumn {
        WithIcon(
            icon = {
                if (container.isRunning) {
                    ActivePoint(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    DeadPoint(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        ) {
            ValueText(
                title = stringResource(R.string.container_command),
                value = container.command,
                modifier = Modifier.weight(1f)
            )
        }

        if (container.pid != 0) {
            ValueText(
                title = stringResource(R.string.container_pid),
                value = container.pid.toString()
            )
        }

        ValueText(
            title = stringResource(R.string.container_last_started),
            value = container.lastStarted
        )

        ValueText(
            title = stringResource(R.string.container_restart_policy),
            value = container.restartPolicy
        )

        if (container.ports.isNotEmpty()) {
            ValuesFlow(
                title = stringResource(R.string.container_ports),
                values = container.ports
            )
        }

        ValuesFlowRow(
            title = stringResource(R.string.labels)
        ) {
            LabelText(
                text = container.createdAt
            )

            if (!container.composeProject.isNullOrEmpty()) {
                LabelText(
                    text = stringResource(
                        R.string.compose_project,
                        container.composeProject.toString()
                    )
                )
            }

            if (!container.composeVersion.isNullOrEmpty()) {
                LabelText(
                    text = stringResource(
                        R.string.compose_version,
                        container.composeVersion.toString()
                    )
                )
            }
        }

        Buttons(
            onStats = onStats,
            enabledState = container.isRunning,
            onLogs = onLogs,
            onOperate = onOperate,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun Buttons(
    onStats: () -> Unit,
    enabledState: Boolean,
    onLogs: () -> Unit,
    onOperate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalIconButton(
            onClick = onStats,
            enabled = enabledState
        ) {
            Icon(
                painter = painterResource(R.drawable.presentation_analytics),
                contentDescription = null
            )
        }

        FilledTonalIconButton(
            onClick = onLogs
        ) {
            Icon(
                painter = painterResource(R.drawable.bug),
                contentDescription = null
            )
        }

        FilledTonalIconButton(
            onClick = onOperate
        ) {
            Icon(
                painter = painterResource(R.drawable.tool),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ImageCard(
    container: UiContainer,
    onClick: () -> Unit
) {
    ValuesColumn(
        onClick = onClick,
        enabled = true
    ) {
        WithIcon(
            painter = painterResource(R.drawable.circle_square)
        ) {
            ValueText(
                title = container.image.ifEmpty { stringResource(R.string.image_untagged) },
                value = container.imageId,
                modifier = Modifier.weight(1f)
            )
        }

        ValueText(
            title = stringResource(R.string.size),
            value = container.imageSize
        )
    }
}

@Composable
private fun VolumeItem(
    mount: UiContainer.MountPoint,
    onClick: () -> Unit
) {
    ValuesColumn(
        onClick = onClick,
        enabled = true
    ) {
        WithIcon(
            painter = painterResource(R.drawable.database)
        ) {
            ValueText(
                title = mount.name,
                value = mount.source,
                modifier = Modifier.weight(1f)
            )
        }

        ValueText(
            title = stringResource(R.string.mount_destination),
            value = mount.destination
        )
    }
}

@Composable
private fun NetworkItem(
    endpoint: UiContainer.EndpointSettings,
    onClick: () -> Unit
) {
    ValuesColumn(
        onClick = onClick,
        enabled = true
    ) {
        WithIcon(
            painter = painterResource(R.drawable.affiliate)
        ) {
            ValueText(
                title = endpoint.name,
                value = endpoint.id,
                modifier = Modifier.weight(1f)
            )
        }

        if (endpoint.subnet.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_subnet),
                value = endpoint.subnet
            )
        }

        if (endpoint.gateway.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_gateway),
                value = endpoint.gateway
            )
        }

        if (endpoint.macAddress.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_mac_address),
                value = endpoint.macAddress
            )
        }

        if (endpoint.dnsNames.isNotEmpty()) {
            ValuesFlow(
                title = stringResource(R.string.network_dns),
                values = endpoint.dnsNames
            )
        }
    }
}

@Composable
private fun OperationBottomSheet(
    onDismiss: () -> Unit,
    onOperate: (Operate) -> Unit,
    state: Container.State
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp)
    ) {
        Text(
            text = stringResource(R.string.operation_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        FlowRow(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OperationButton(
                onClick = {
                    onOperate(if (state.isExited) Operate.Start else Operate.Stop)
                },
                enabled = state.isRunning || state.isExited,
                painter = painterResource(
                    if (state.isExited) {
                        R.drawable.player_play
                    } else {
                        R.drawable.player_stop
                    }
                ),
                label = stringResource(
                    if (state.isExited) {
                        R.string.operation_start
                    } else {
                        R.string.operation_stop
                    }
                )
            )

            OperationButton(
                onClick = {
                    onOperate(if (state.isPaused) Operate.Unpause else Operate.Pause)
                },
                enabled = state.isRunning || state.isPaused,
                painter = painterResource(
                    if (state.isPaused) {
                        R.drawable.player_play
                    } else {
                        R.drawable.player_pause
                    }
                ),
                label = stringResource(
                    if (state.isPaused) {
                        R.string.operation_unpause
                    } else {
                        R.string.operation_pause
                    }
                )
            )

            OperationButton(
                onClick = { onOperate(Operate.Restart) },
                painter = painterResource(R.drawable.restore),
                label = stringResource(R.string.operation_restart)
            )

            OperationButton(
                onClick = { onOperate(Operate.Up) },
                painter = painterResource(R.drawable.arrow_up_to_arc),
                label = stringResource(R.string.operation_up)
            )

            OperationButton(
                onClick = { onOperate(Operate.Remove) },
                enabled = state.isExited,
                painter = painterResource(R.drawable.trash),
                label = stringResource(R.string.operation_remove)
            )
        }
    }
}

@Composable
private fun TopBar(
    onRefresh: () -> Unit,
    name: String,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { AnimatedText(text = name) },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigateUp() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRefresh
            ) {
                Icon(
                    painter = painterResource(R.drawable.refresh),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}