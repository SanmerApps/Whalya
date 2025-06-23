package dev.sanmer.whalya.ui.screens.inspect.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.ui.inspect.UiContainerStats
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.WithIcon
import dev.sanmer.whalya.ui.ktx.plus
import dev.sanmer.whalya.viewmodel.ContainerStatsViewModel

@Composable
fun ContainerStatsScreen(
    viewModel: ContainerStatsViewModel = hiltViewModel(),
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                isRunning = viewModel.isRunning,
                onClick = { viewModel.update { !it } },
                navController = navController
            )
        }
    ) { contentPadding ->
        when (val data = viewModel.data) {
            LoadData.Pending, LoadData.Loading -> Loading(
                modifier = Modifier.padding(contentPadding)
            )

            is LoadData.Success<UiContainerStats> -> ContainerStatsContent(
                stats = data.value,
                contentPadding = contentPadding
            )

            is LoadData.Failure -> Failed(
                error = data.error,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
private fun ContainerStatsContent(
    stats: UiContainerStats,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Row(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(contentPadding + PaddingValues(20.dp)),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ContainerCard(
                stats = stats
            )

            CpuCard(
                stats = stats
            )

            NetworkCard(
                stats = stats
            )

            TimestampCard(
                stats = stats
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProcessIDsCard(
                stats = stats
            )

            MemoryCard(
                stats = stats
            )

            BlockCard(
                stats = stats
            )
        }
    }
}

@Composable
private fun ContainerCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.box)
        ) {
            Text(
                text = stats.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = stats.id,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun CpuCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.cpu)
        ) {
            Text(
                text = stringResource(R.string.stats_cpu),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "%.2f".format(stats.cpuPercent * 100) + '%',
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun MemoryCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.playstation_circle)
        ) {
            Text(
                text = stringResource(R.string.stats_memory),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "${stats.memoryUsage} / ${stats.memoryLimit}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun NetworkCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.affiliate)
        ) {
            Text(
                text = stringResource(R.string.stats_network),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "${stats.networkReceived} / ${stats.networkSent}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun BlockCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.blocks)
        ) {
            Text(
                text = stringResource(R.string.stats_block),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "${stats.blockRead} / ${stats.blockWrite}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ProcessIDsCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.brackets_angle)
        ) {
            Text(
                text = stringResource(R.string.stats_process_ids),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = stats.pids.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun TimestampCard(
    stats: UiContainerStats,
    modifier: Modifier = Modifier
) {
    ValuesColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WithIcon(
            painter = painterResource(R.drawable.activity)
        ) {
            Text(
                text = stringResource(R.string.stats_timestamp),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = stats.readAt.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun BottomBar(
    isRunning: Boolean,
    onClick: () -> Unit,
    navController: NavController
) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = { navController.navigateUp() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClick,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    painter = painterResource(
                        if (isRunning) R.drawable.cloud else R.drawable.cloud_off
                    ),
                    contentDescription = null
                )
            }
        }
    )
}