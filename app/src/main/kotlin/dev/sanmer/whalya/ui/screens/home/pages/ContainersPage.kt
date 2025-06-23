package dev.sanmer.whalya.ui.screens.home.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.ui.component.ActivePoint
import dev.sanmer.whalya.ui.component.DeadPoint
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.ValuesFlow
import dev.sanmer.whalya.ui.component.ValuesFlowRow
import dev.sanmer.whalya.ui.component.WithIcon
import dev.sanmer.whalya.ui.ktx.navigateSingleTopTo
import dev.sanmer.whalya.ui.main.Screen

@Composable
fun ContainersPage(
    navController: NavController,
    data: LoadData<List<UiContainer>>
) {
    when (data) {
        LoadData.Pending, LoadData.Loading -> Loading()
        is LoadData.Success<List<UiContainer>> -> ContainerList(
            navController = navController,
            list = data.value
        )

        is LoadData.Failure -> Failed(data.error)
    }
}

@Composable
private fun ContainerList(
    navController: NavController,
    list: List<UiContainer>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list) {
            ContainerItem(it) {
                navController.navigateSingleTopTo(Screen.Container(it.id))
            }
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
                title = container.name,
                value = container.id,
                modifier = Modifier.weight(1f)
            )
        }

        if (container.exposedPorts.isNotEmpty()) {
            ValuesFlow(
                title = stringResource(R.string.container_ports),
                values = container.exposedPorts
            )
        }

        if (container.networks.isNotEmpty()) {
            ValuesFlow(
                title = stringResource(R.string.container_networks),
                values = container.networks
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