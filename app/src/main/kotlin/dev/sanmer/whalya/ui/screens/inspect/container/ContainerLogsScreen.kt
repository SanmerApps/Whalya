package dev.sanmer.whalya.ui.screens.inspect.container

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.core.response.container.ContainerLog
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.ansi.appendANSIOrDefault
import dev.sanmer.whalya.ui.ktx.plus
import dev.sanmer.whalya.viewmodel.ContainerLogsViewModel

@Composable
fun ContainerLogsScreen(
    viewModel: ContainerLogsViewModel = hiltViewModel(),
    navController: NavController
) {
    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::toggleSearch
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            BottomBar(
                isRunning = viewModel.isRunning,
                onToggleRunning = viewModel::toggleRunning,
                isSearch = viewModel.isSearch,
                onToggleSearch = viewModel::toggleSearch,
                enabledSearch = viewModel.data.isSuccess,
                onSearch = viewModel::search,
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
    onToggleRunning: () -> Unit,
    isSearch: Boolean,
    onToggleSearch: () -> Unit,
    enabledSearch: Boolean,
    onSearch: (String) -> Unit,
    navController: NavController
) {
    var key by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(isSearch) {
        if (isSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        onDispose {
            key = ""
            keyboardController?.hide()
        }
    }

    BottomAppBar(
        actions = {
            IconButton(
                onClick = {
                    if (isSearch) {
                        onToggleSearch()
                    } else {
                        navController.navigateUp()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null
                )
            }

            if (isSearch) {
                BasicTextField(
                    value = key,
                    onValueChange = {
                        onSearch(it)
                        key = it
                    },
                    textStyle = LocalTextStyle.current,
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = OutlinedTextFieldDefaults.MinWidth
                        )
                        .focusRequester(focusRequester)
                )
            } else {
                IconButton(
                    onClick = onToggleSearch,
                    enabled = enabledSearch
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onToggleRunning,
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