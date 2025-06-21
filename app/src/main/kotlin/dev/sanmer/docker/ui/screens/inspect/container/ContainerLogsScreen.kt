package dev.sanmer.docker.ui.screens.inspect.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.core.response.container.ContainerLog
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.ui.component.Failed
import dev.sanmer.docker.ui.component.Loading
import dev.sanmer.docker.ui.component.ansi.appendANSIOrDefault
import dev.sanmer.docker.ui.ktx.plus
import dev.sanmer.docker.viewmodel.ContainerLogsViewModel

@Composable
fun ContainerLogsScreen(
    viewModel: ContainerLogsViewModel = hiltViewModel(),
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

            is LoadData.Success<List<ContainerLog>> -> ContainerLogsContent(
                list = data.value,
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
private fun ContainerLogsContent(
    list: List<ContainerLog>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    SelectionContainer {
        LazyColumn(
            state = listState,
            modifier = modifier,
            contentPadding = contentPadding + PaddingValues(all = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            reverseLayout = true
        ) {
            items(list) {
                val text by remember(it) {
                    derivedStateOf {
                        buildAnnotatedString {
                            appendANSIOrDefault(it.content)
                        }
                    }
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                        .copy(fontFamily = FontFamily.Monospace)
                )
            }
        }
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