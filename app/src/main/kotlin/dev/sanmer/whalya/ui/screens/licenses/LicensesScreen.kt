package dev.sanmer.whalya.ui.screens.licenses

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.ui.UiLicense
import dev.sanmer.whalya.ui.component.Failed
import dev.sanmer.whalya.ui.component.LabelText
import dev.sanmer.whalya.ui.component.Loading
import dev.sanmer.whalya.ui.component.SearchContent
import dev.sanmer.whalya.ui.component.ValueText
import dev.sanmer.whalya.ui.component.ValuesColumn
import dev.sanmer.whalya.ui.ktx.plus
import org.koin.androidx.compose.koinViewModel

@Composable
fun LicensesScreen(
    viewModel: LicensesViewModel = koinViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::toggleSearch
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                onToggleSearch = viewModel::toggleSearch,
                enabledSearch = viewModel.data.isSuccess,
                onSearch = viewModel::search,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        when (val data = viewModel.data) {
            LoadData.Pending, LoadData.Loading -> Loading(
                modifier = Modifier.padding(contentPadding)
            )

            is LoadData.Success<List<UiLicense>> -> LicensesContent(
                list = data.value,
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
private fun LicensesContent(
    list: List<UiLicense>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(all = 20.dp) + contentPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list) {
            LicenseItem(it)
        }
    }
}

@Composable
private fun LicenseItem(
    license: UiLicense
) {
    val context = LocalContext.current

    ValuesColumn(
        onClick = {
            context.startActivity(
                Intent.parseUri(license.url, Intent.URI_INTENT_SCHEME)
            )
        },
        enabled = license.hasUrl,
        modifier = Modifier.fillMaxWidth()
    ) {
        ValueText(
            title = license.name,
            value = license.dependency
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LabelText(
                text = license.version
            )

            license.spdxLicenses.forEach {
                LabelText(
                    text = it.name
                )
            }

            license.unknownLicenses.forEach {
                LabelText(
                    text = it.name.ifEmpty { it.url }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    isSearch: Boolean,
    onToggleSearch: () -> Unit,
    enabledSearch: Boolean,
    onSearch: (String) -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            if (isSearch) {
                SearchContent(
                    onSearch = onSearch,
                    textStyle = MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Normal)
                )
            } else {
                Text(text = stringResource(R.string.license_title))
            }
        },
        navigationIcon = {
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
        },
        actions = {
            if (!isSearch) {
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
        scrollBehavior = scrollBehavior
    )
}