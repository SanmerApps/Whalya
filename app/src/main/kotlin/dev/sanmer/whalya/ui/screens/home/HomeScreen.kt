package dev.sanmer.whalya.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.whalya.R
import dev.sanmer.whalya.ktx.messageOrName
import dev.sanmer.whalya.ktx.sizeBySI
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.ui.component.AnimatedIcon
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.ktx.bottom
import dev.sanmer.whalya.ui.ktx.surface
import dev.sanmer.whalya.ui.screens.home.pages.ContainersPage
import dev.sanmer.whalya.ui.screens.home.pages.ImagesPage
import dev.sanmer.whalya.ui.screens.home.pages.NetworksPage
import dev.sanmer.whalya.ui.screens.home.pages.SystemPage
import dev.sanmer.whalya.ui.screens.home.pages.VolumesPage
import dev.sanmer.whalya.viewmodel.HomeViewModel
import dev.sanmer.whalya.viewmodel.HomeViewModel.Prune
import dev.sanmer.whalya.viewmodel.HomeViewModel.PruneResult
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val pagerState = rememberPagerState { 5 }
    val scrollBehaviors = List(pagerState.pageCount) { TopAppBarDefaults.pinnedScrollBehavior() }
    val scrollBehavior by remember { derivedStateOf { scrollBehaviors[pagerState.targetPage] } }

    val loadData = remember {
        listOf(
            viewModel::loadSystemData,
            viewModel::loadContainersData,
            viewModel::loadImagesData,
            viewModel::loadNetworksData,
            viewModel::loadVolumesData
        )
    }

    var prune by remember { mutableStateOf(false) }
    if (prune) {
        PruneBottomSheet(
            onDismiss = { prune = false },
            getData = viewModel::getPruneData,
            onPrune = viewModel::prune,
            onClear = viewModel::clearPruneData
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                name = viewModel.name,
                onRefresh = loadData[pagerState.targetPage],
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomBar(
                state = pagerState
            )
        }
    ) { contentPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalAlignment = Alignment.Top,
            userScrollEnabled = false,
            pageNestedScrollConnection = scrollBehavior.nestedScrollConnection
        ) { page ->
            when (page) {
                0 -> SystemPage(
                    data = viewModel.system,
                    onPrune = { prune = true }
                )

                1 -> ContainersPage(
                    navController = navController,
                    data = viewModel.containers
                )

                2 -> ImagesPage(
                    navController = navController,
                    data = viewModel.images
                )

                3 -> NetworksPage(
                    navController = navController,
                    data = viewModel.networks
                )

                4 -> VolumesPage(
                    navController = navController,
                    data = viewModel.volumes,

                    )
            }
        }
    }
}

@Composable
private fun PruneBottomSheet(
    onDismiss: () -> Unit,
    getData: (Prune) -> LoadData<PruneResult>,
    onPrune: (Prune) -> Unit,
    onClear: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    DisposableEffect(true) {
        onDispose(onClear)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp)
    ) {
        Text(
            text = stringResource(R.string.prune_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier.padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            PruneItem(
                bar = BottomBar.Containers,
                subtitle = R.string.prune_containers,
                data = getData(Prune.Containers),
                onClick = { onPrune(Prune.Containers) }
            )

            PruneItem(
                bar = BottomBar.Images,
                subtitle = R.string.prune_images,
                data = getData(Prune.Images),
                onClick = { onPrune(Prune.Images) }
            )

            PruneItem(
                bar = BottomBar.Networks,
                subtitle = R.string.prune_networks,
                data = getData(Prune.Networks),
                onClick = { onPrune(Prune.Networks) }
            )

            PruneItem(
                bar = BottomBar.Volumes,
                subtitle = R.string.prune_volumes,
                data = getData(Prune.Volumes),
                onClick = { onPrune(Prune.Volumes) }
            )
        }
    }
}

@Composable
private fun PruneItem(
    bar: BottomBar,
    @StringRes subtitle: Int,
    data: LoadData<PruneResult>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .surface(
                shape = MaterialTheme.shapes.large,
                backgroundColor = MaterialTheme.colorScheme.surface,
                border = CardDefaults.outlinedCardBorder()
            )
            .clickable(
                onClick = onClick,
                enabled = true
            )
            .padding(all = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        AnimatedIcon(
            isActive = data.isLoading,
            painter = painterResource(bar.icon),
            contentDescription = null
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(bar.label),
                style = MaterialTheme.typography.titleMedium
            )

            PruneSubtitle(
                subtitle = subtitle,
                data = data
            )
        }
    }
}

@Composable
private fun PruneSubtitle(
    @StringRes subtitle: Int,
    data: LoadData<PruneResult>
) {
    when (data) {
        LoadData.Pending, LoadData.Loading -> Text(
            text = stringResource(subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        is LoadData.Success<PruneResult> -> FlowRow(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            LabelText(
                text = stringResource(R.string.prune_removed, data.value.sizeDeleted)
            )

            LabelText(
                text = stringResource(
                    R.string.prune_reclaimed,
                    data.value.spaceReclaimed.sizeBySI()
                )
            )
        }

        is LoadData.Failure -> Text(
            text = data.error.messageOrName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun TopBar(
    name: String,
    onRefresh: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(text = name) },
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

@Composable
private fun BottomBar(
    state: PagerState
) {
    val scope = rememberCoroutineScope()

    NavigationBar {
        BottomBar.entries.forEachIndexed { page, bottomBar ->
            NavigationBarItem(
                selected = state.targetPage == page,
                onClick = {
                    scope.launch {
                        state.animateScrollToPage(
                            page = page,
                            animationSpec = spring(stiffness = Spring.StiffnessLow)
                        )
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(bottomBar.icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(bottomBar.label)
                    )
                }
            )
        }
    }
}

private enum class BottomBar(
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    System(
        icon = R.drawable.browser,
        label = R.string.home_system
    ),

    Containers(
        icon = R.drawable.box,
        label = R.string.home_containers
    ),

    Images(
        icon = R.drawable.circle_square,
        label = R.string.home_images
    ),

    Networks(
        icon = R.drawable.affiliate,
        label = R.string.home_networks
    ),

    Volumes(
        icon = R.drawable.database,
        label = R.string.home_volumes
    )
}