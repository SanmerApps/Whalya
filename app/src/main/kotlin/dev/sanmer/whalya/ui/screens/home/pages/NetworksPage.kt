package dev.sanmer.whalya.ui.screens.home.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import dev.sanmer.whalya.model.ui.home.UiNetwork
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.ValuesFlowRow
import dev.sanmer.whalya.ui.ktx.navigateSingleTopTo
import dev.sanmer.whalya.ui.main.Screen

@Composable
fun NetworksPage(
    navController: NavController,
    data: LoadData<List<UiNetwork>>
) {
    when (data) {
        LoadData.Pending, LoadData.Loading -> Loading()
        is LoadData.Success<List<UiNetwork>> -> NetworkList(
            navController = navController,
            list = data.value
        )

        is LoadData.Failure -> Failed(data.error)
    }
}

@Composable
private fun NetworkList(
    navController: NavController,
    list: List<UiNetwork>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list) {
            NetworkItem(it) {
                navController.navigateSingleTopTo(Screen.Network(it.id))
            }
        }
    }
}

@Composable
private fun NetworkItem(
    network: UiNetwork,
    onClick: () -> Unit
) {
    ValuesColumn(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = true
    ) {
        ValueText(
            title = network.name,
            value = network.id
        )

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
    }
}