package dev.sanmer.whalya.ui.screens.inspect.volume

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.model.ui.inspect.UiVolume
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.OperationButton
import dev.sanmer.whalya.ui.component.OperationResultBottomSheet
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.ValuesFlowRow
import dev.sanmer.whalya.ui.component.WithIcon
import dev.sanmer.whalya.ui.ktx.bottom
import dev.sanmer.whalya.ui.ktx.navigatePopTo
import dev.sanmer.whalya.ui.ktx.plus
import dev.sanmer.whalya.ui.main.Screen
import dev.sanmer.whalya.viewmodel.HomeViewModel
import dev.sanmer.whalya.viewmodel.HomeViewModel.Load.Default.setLoad
import dev.sanmer.whalya.viewmodel.VolumeViewModel
import dev.sanmer.whalya.viewmodel.VolumeViewModel.BottomSheet
import dev.sanmer.whalya.viewmodel.VolumeViewModel.Operate

@Composable
fun VolumeScreen(
    viewModel: VolumeViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(viewModel.result) {
        when (val result = viewModel.result) {
            is LoadData.Success<Operate> -> {
                if (result.value.isDestroyed) {
                    viewModel.update(BottomSheet.Closed)
                    navController.setLoad(HomeViewModel.Load.Volumes)
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
            enabledRemove = viewModel.containers.isEmpty()
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

            is LoadData.Success<UiVolume> -> VolumeContent(
                navController = navController,
                volume = data.value,
                containers = viewModel.containers,
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
private fun VolumeContent(
    navController: NavController,
    volume: UiVolume,
    containers: List<UiContainer>,
    onOperate: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.animateContentSize(),
        contentPadding = contentPadding + PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            VolumeCard(
                volume = volume,
                onOperate = onOperate
            )
        }

        items(containers) { container ->
            ContainerItem(
                container = container
            ) {
                navController.navigatePopTo(Screen.Container(container.id))
            }
        }
    }
}

@Composable
private fun VolumeCard(
    volume: UiVolume,
    onOperate: () -> Unit
) {
    ValuesColumn {
        WithIcon(
            painter = painterResource(R.drawable.database)
        ) {
            ValueText(
                title = stringResource(R.string.driver),
                value = volume.driver,
                modifier = Modifier.weight(1f)
            )
        }

        ValueText(
            title = stringResource(R.string.mount_point),
            value = volume.mountPoint
        )

        ValuesFlowRow(
            title = stringResource(R.string.labels)
        ) {
            LabelText(
                text = volume.createdAt
            )

            if (!volume.composeProject.isNullOrEmpty()) {
                LabelText(
                    text = stringResource(
                        R.string.compose_project,
                        volume.composeProject.toString()
                    )
                )
            }

            if (!volume.composeVersion.isNullOrEmpty()) {
                LabelText(
                    text = stringResource(
                        R.string.compose_version,
                        volume.composeVersion.toString()
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
    container: UiContainer,
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
    }
}

@Composable
private fun OperationBottomSheet(
    onDismiss: () -> Unit,
    onOperate: (Operate) -> Unit,
    enabledRemove: Boolean
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
                onClick = { onOperate(Operate.Remove) },
                enabled = enabledRemove,
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
        title = { Text(text = name) },
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