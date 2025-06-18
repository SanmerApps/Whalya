package dev.sanmer.docker.ui.screens.inspect.container

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.core.docker.response.container.Container
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.ui.inspect.UiContainer
import dev.sanmer.docker.ui.component.ActivePoint
import dev.sanmer.docker.ui.component.AnimatedText
import dev.sanmer.docker.ui.component.DeadPoint
import dev.sanmer.docker.ui.component.Failed
import dev.sanmer.docker.ui.component.LabelText
import dev.sanmer.docker.ui.component.Loading
import dev.sanmer.docker.ui.component.OperationButton
import dev.sanmer.docker.ui.component.OperationResultBottomSheet
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.component.ValuesColumn
import dev.sanmer.docker.ui.component.ValuesFlow
import dev.sanmer.docker.ui.component.ValuesFlowRow
import dev.sanmer.docker.ui.component.WithIcon
import dev.sanmer.docker.ui.ktx.bottom
import dev.sanmer.docker.ui.ktx.navigatePopTo
import dev.sanmer.docker.ui.ktx.navigateSingleTopTo
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.ui.main.Screen
import dev.sanmer.docker.viewmodel.ContainerViewModel
import dev.sanmer.docker.viewmodel.ContainerViewModel.BottomSheet
import dev.sanmer.docker.viewmodel.ContainerViewModel.Operate

@Composable
fun ContainerScreen(
    viewModel: ContainerViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(viewModel.data) {
        if (viewModel.data.isFailure && viewModel.result.isSuccess) {
            viewModel.update(BottomSheet.Closed)
            navController.navigateUp()
        }
        onDispose {}
    }

    when (viewModel.bottomSheet) {
        BottomSheet.Closed -> {}
        BottomSheet.Operate -> OperationBottomSheet(
            onDismiss = { viewModel.update(BottomSheet.Closed) },
            state = viewModel.state,
            onOperate = viewModel::operate
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

        if (container.restartPolicy.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.container_restart_policy),
                value = container.restartPolicy
            )
        }

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
    state: Container.State,
    onOperate: (Operate) -> Unit
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
                onClick = { onOperate(Operate.Remove) },
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