package dev.sanmer.docker.ui.screens.home.pages

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
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.ui.home.UiImage
import dev.sanmer.docker.ui.component.Failed
import dev.sanmer.docker.ui.component.LabelText
import dev.sanmer.docker.ui.component.Loading
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.component.ValuesColumn
import dev.sanmer.docker.ui.component.ValuesFlowRow
import dev.sanmer.docker.ui.ktx.navigateSingleTopTo
import dev.sanmer.docker.ui.main.Screen

@Composable
fun ImagesPage(
    navController: NavController,
    data: LoadData<List<UiImage>>
) {
    when (data) {
        LoadData.Pending, LoadData.Loading -> Loading()
        is LoadData.Success<List<UiImage>> -> ImageList(
            navController = navController,
            list = data.value
        )

        is LoadData.Failure -> Failed(data.error)
    }
}

@Composable
private fun ImageList(
    navController: NavController,
    list: List<UiImage>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list) {
            ImageItem(it) {
                navController.navigateSingleTopTo(Screen.Image(it.id))
            }
        }
    }
}

@Composable
private fun ImageItem(
    image: UiImage,
    onClick: () -> Unit
) {
    ValuesColumn(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = true
    ) {
        ValueText(
            title = image.name ?: stringResource(R.string.image_untagged),
            value = image.id
        )

        ValueText(
            title = stringResource(R.string.image_digests),
            value = image.repoDigests
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
    }
}