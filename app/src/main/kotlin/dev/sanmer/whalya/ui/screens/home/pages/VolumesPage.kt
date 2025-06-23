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
import dev.sanmer.whalya.model.ui.home.UiVolume
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.component.ValuesFlowRow
import dev.sanmer.whalya.ui.ktx.navigateSingleTopTo
import dev.sanmer.whalya.ui.main.Screen

@Composable
fun VolumesPage(
    navController: NavController,
    data: LoadData<List<UiVolume>>
) {
    when (data) {
        LoadData.Pending, LoadData.Loading -> Loading()
        is LoadData.Success<List<UiVolume>> -> VolumeList(
            navController = navController,
            list = data.value
        )

        is LoadData.Failure -> Failed(data.error)
    }
}

@Composable
private fun VolumeList(
    navController: NavController,
    list: List<UiVolume>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list) {
            VolumeItem(it) {
                navController.navigateSingleTopTo(Screen.Volume(it.original.name))
            }
        }
    }
}

@Composable
private fun VolumeItem(
    volume: UiVolume,
    onClick: () -> Unit
) {
    ValuesColumn(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = true
    ) {
        ValueText(
            title = volume.name,
            value = volume.mountPoint
        )

        ValuesFlowRow(
            title = stringResource(R.string.labels)
        ) {
            LabelText(
                text = volume.createdAt
            )

            LabelText(
                text = stringResource(R.string.scope, volume.scope)
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
    }
}