package dev.sanmer.docker.ui.screens.inspect.network

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.ui.inspect.UiNetwork
import dev.sanmer.docker.ui.component.AnimatedText
import dev.sanmer.docker.ui.component.Failed
import dev.sanmer.docker.ui.component.LabelText
import dev.sanmer.docker.ui.component.Loading
import dev.sanmer.docker.ui.component.OperationButton
import dev.sanmer.docker.ui.component.OperationResultBottomSheet
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.component.ValuesColumn
import dev.sanmer.docker.ui.component.ValuesFlowRow
import dev.sanmer.docker.ui.component.WithIcon
import dev.sanmer.docker.ui.ktx.bottom
import dev.sanmer.docker.ui.ktx.navigatePopTo
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.ui.main.Screen
import dev.sanmer.docker.viewmodel.NetworkViewModel
import dev.sanmer.docker.viewmodel.NetworkViewModel.BottomSheet

@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (viewModel.bottomSheet) {
        BottomSheet.Closed -> {}
        BottomSheet.Operate -> OperationBottomSheet(
            onDismiss = { viewModel.update(BottomSheet.Closed) },
            onRemove = viewModel::remove
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

            is LoadData.Success<UiNetwork> -> NetworkContent(
                navController = navController,
                network = data.value,
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
private fun NetworkContent(
    navController: NavController,
    network: UiNetwork,
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
            NetworkCard(
                network = network,
                onOperate = onOperate
            )
        }

        items(network.containers) { container ->
            ContainerItem(
                container = container
            ) {
                navController.navigatePopTo(Screen.Container(container.id))
            }
        }
    }
}

@Composable
private fun NetworkCard(
    network: UiNetwork,
    onOperate: () -> Unit
) {
    ValuesColumn {
        WithIcon(
            painter = painterResource(R.drawable.affiliate)
        ) {
            ValueText(
                title = stringResource(R.string.driver),
                value = network.driver,
                modifier = Modifier.weight(1f)
            )
        }

        if (network.subnet.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_subnet),
                value = network.subnet
            )
        }

        if (network.gateway.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_gateway),
                value = network.gateway
            )
        }

        ValuesFlowRow(
            title = stringResource(R.string.labels)
        ) {
            LabelText(
                text = network.createdAt
            )

            LabelText(
                text = stringResource(R.string.scope, network.scope)
            )

            if (!network.composeProject.isNullOrEmpty()) {
                LabelText(
                    text = stringResource(
                        R.string.compose_project,
                        network.composeProject.toString()
                    )
                )
            }

            if (!network.composeVersion.isNullOrEmpty()) {
                LabelText(
                    text = stringResource(
                        R.string.compose_version,
                        network.composeVersion.toString()
                    )
                )
            }
        }

        Buttons(
            onOperate = onOperate,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun Buttons(
    onOperate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
private fun ContainerItem(
    container: UiNetwork.NetworkContainer,
    onClick: () -> Unit
) {
    ValuesColumn(
        onClick = onClick,
        enabled = true
    ) {
        WithIcon(
            painter = painterResource(R.drawable.box)
        ) {
            ValueText(
                title = container.name,
                value = container.id,
                modifier = Modifier.weight(1f)
            )
        }

        if (container.ipv4Address.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_ipv4_address),
                value = container.ipv4Address
            )
        }

        if (container.ipv6Address.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_ipv6_address),
                value = container.ipv6Address
            )
        }

        if (container.macAddress.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.network_mac_address),
                value = container.macAddress
            )
        }
    }
}

@Composable
private fun OperationBottomSheet(
    onDismiss: () -> Unit,
    onRemove: () -> Unit
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
                onClick = onRemove,
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