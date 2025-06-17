package dev.sanmer.docker.ui.screens.inspect.image

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.core.docker.Labels
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.ui.home.UiContainer
import dev.sanmer.docker.model.ui.inspect.UiImage
import dev.sanmer.docker.ui.component.AnimatedText
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
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.ui.ktx.surface
import dev.sanmer.docker.ui.main.Screen
import dev.sanmer.docker.viewmodel.ImageViewModel
import dev.sanmer.docker.viewmodel.ImageViewModel.BottomSheet

@Composable
fun ImageScreen(
    viewModel: ImageViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (viewModel.bottomSheet) {
        BottomSheet.Closed -> {}
        BottomSheet.Layer -> LayersBottomSheet(
            onDismiss = { viewModel.update(BottomSheet.Closed) },
            histories = viewModel.histories
        )

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
                name = viewModel.name ?: stringResource(R.string.image_untagged),
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        when (val date = viewModel.data) {
            LoadData.Pending, LoadData.Loading -> Loading(
                modifier = Modifier.padding(contentPadding)
            )

            is LoadData.Success<UiImage> -> ImageContent(
                navController = navController,
                image = date.value,
                containers = viewModel.containers,
                onLayer = { viewModel.update(BottomSheet.Layer) },
                onOperate = { viewModel.update(BottomSheet.Operate) },
                contentPadding = contentPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )

            is LoadData.Failure -> Failed(
                error = date.error,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
private fun ImageContent(
    navController: NavController,
    image: UiImage,
    containers: List<UiContainer>,
    onLayer: () -> Unit,
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
            ImageCard(
                image = image,
                onLayer = onLayer,
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

        if (image.labels.isNotEmpty()) item {
            LabelsCard(
                image = image
            )
        }
    }
}

@Composable
private fun ImageCard(
    image: UiImage,
    onLayer: () -> Unit,
    onOperate: () -> Unit
) {
    ValuesColumn {
        WithIcon(
            painter = painterResource(R.drawable.circle_square)
        ) {
            ValueText(
                title = stringResource(R.string.image_command),
                value = image.command,
                modifier = Modifier.weight(1f)
            )
        }

        if (image.repoTags.isNotEmpty()) {
            ValueText(
                title = stringResource(R.string.image_tags),
                value = image.repoTags
            )
        }

        ValueText(
            title = stringResource(R.string.image_digests),
            value = image.repoDigests
        )

        ValuesFlow(
            title = stringResource(R.string.os_platform),
            values = image.platform
        )

        ValuesFlowRow(
            title = stringResource(R.string.labels)
        ) {
            if (!image.ociVersion.isNullOrEmpty()) {
                LabelText(
                    text = image.ociVersion.toString()
                )
            }

            if (!image.ociLicenses.isNullOrEmpty()) {
                LabelText(
                    text = image.ociLicenses.toString()
                )
            }

            LabelText(
                text = image.createdAt
            )

            LabelText(
                text = image.size
            )
        }

        Buttons(
            onLayer = onLayer,
            onOperate = onOperate,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun Buttons(
    onLayer: () -> Unit,
    onOperate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalIconButton(
            onClick = onLayer
        ) {
            Icon(
                painter = painterResource(R.drawable.stack_2),
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
private fun LayersBottomSheet(
    onDismiss: () -> Unit,
    histories: List<UiImage.History>
) {
    if (histories.isEmpty()) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var index by remember { mutableIntStateOf(-1) }
    if (index != -1) {
        LayerBottomSheet(
            onDismiss = { index = -1 },
            history = histories[index]
        )
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp)
    ) {
        Text(
            text = stringResource(R.string.image_layers),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            modifier = Modifier
                .padding(all = 20.dp)
                .surface(
                    shape = MaterialTheme.shapes.large,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(all = 20.dp)
        ) {
            itemsIndexed(histories) { i, history ->
                LayerItem(history) {
                    index = i
                }
            }
        }
    }
}

@Composable
private fun LayerBottomSheet(
    onDismiss: () -> Unit,
    history: UiImage.History
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp)
    ) {
        Text(
            text = history.createdAt,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        SelectionContainer {
            Text(
                text = history.createdBy,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(all = 20.dp)
                    .fillMaxWidth()
                    .surface(
                        shape = MaterialTheme.shapes.medium,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(all = 20.dp)
            )
        }
    }
}

@Composable
private fun LayerItem(
    history: UiImage.History,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = history.createdBy,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        LabelText(
            text = history.size,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = onClick
                )
        )
    }
}

@Composable
private fun LabelsCard(
    image: UiImage
) {
    val first by remember {
        derivedStateOf { image.labels.first() }
    }

    val values by remember {
        derivedStateOf { image.labels.drop(1) }
    }

    ValuesColumn {
        WithIcon(
            painter = painterResource(R.drawable.tag)
        ) {
            ValueText(
                title = labelName(first.first),
                value = first.second,
                modifier = Modifier.weight(1f),
                selectable = true
            )
        }

        values.forEach { (name, value) ->
            ValueText(
                title = labelName(name),
                value = value,
                selectable = true
            )
        }
    }
}

@Composable
private fun labelName(
    name: String
): String {
    val resId by remember { derivedStateOf { Labels(name) } }
    return resId?.let { stringResource(it) } ?: name
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