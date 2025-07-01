package dev.sanmer.whalya.ui.screens.servers

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.core.JsonCompat.encodeJson
import dev.sanmer.core.response.system.SystemVersion
import dev.sanmer.whalya.Const
import dev.sanmer.whalya.R
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.ui.component.CheckIcon
import dev.sanmer.whalya.ui.ktx.copy
import dev.sanmer.whalya.ui.ktx.horizontal
import dev.sanmer.whalya.ui.screens.servers.AddServerViewModel.Control
import dev.sanmer.whalya.ui.screens.servers.AddServerViewModel.MutualTLS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddServerScreen(
    viewModel: AddServerViewModel = koinViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(viewModel.control) {
        if (viewModel.control.isSaved) {
            navController.navigateUp()
        }
        onDispose {}
    }

    BackHandler(
        enabled = viewModel.control.isNoEdit,
        onBack = { viewModel.update(Control.Edit) }
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                control = viewModel.control,
                setControl = viewModel::update,
                onDelete = viewModel::delete,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                control = viewModel.control,
                setControl = viewModel::update,
                onConnect = viewModel::connect,
                onSave = viewModel::save
            )
        }
    ) { contentPadding ->
        Crossfade(
            targetState = viewModel.control
        ) { control ->
            if (control.isNoEdit) {
                ConnectContent(
                    viewModel = viewModel,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(contentPadding)
                )
            } else {
                AddServerContent(
                    viewModel = viewModel,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(contentPadding)
                )
            }
        }
    }
}

@Composable
private fun ConnectContent(
    viewModel: AddServerViewModel,
    modifier: Modifier = Modifier
) {
    when (val data = viewModel.data) {
        LoadData.Pending, LoadData.Loading -> {}
        is LoadData.Success<SystemVersion> -> {
            val value by remember {
                derivedStateOf {
                    data.value.encodeJson(pretty = true)
                }
            }

            TextCard(
                value = value,
                modifier = modifier
            )
        }

        is LoadData.Failure -> {
            val value by remember {
                derivedStateOf {
                    data.error.stackTraceToString()
                }
            }

            TextCard(
                value = value,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TextCard(
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .border(
                    border = CardDefaults.outlinedCardBorder(),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(all = 20.dp),
            maxLines = 35,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddServerContent(
    viewModel: AddServerViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = viewModel.control.isConnecting
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .height(5.dp)
                    .padding(horizontal = 2.dp)
                    .fillMaxWidth()
            )
        }

        ValueTextField(
            value = viewModel.input.name,
            onValueChange = { name ->
                viewModel.input { it.copy(name = name) }
            },
            title = stringResource(R.string.server_name),
            modifier = Modifier.padding(all = 20.dp)
        )

        ValueTextField(
            value = viewModel.input.apiEndpoint,
            onValueChange = { apiEndpoint ->
                viewModel.input { it.copy(apiEndpoint = apiEndpoint) }
            },
            title = stringResource(R.string.server_api_endpoint),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        MutualTLSTextField(
            cert = viewModel.input.cert,
            onCertChange = { cert ->
                viewModel.input { it.copy(cert = cert) }
            },
            value = viewModel.cert,
            onValueChange = viewModel::inputCert,
            contentPadding = PaddingValues(all = 20.dp)
        )
    }
}

@Composable
private fun ValueTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MutualTLSTextField(
    cert: MutualTLS,
    onCertChange: (MutualTLS) -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            text = stringResource(R.string.server_mutual_tls),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(contentPadding.copy(bottom = 0.dp))
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .horizontalScroll(rememberScrollState())
                .padding(contentPadding.horizontal())
        ) {
            MutualTLS.entries.forEachIndexed { index, value ->
                SegmentedButton(
                    selected = cert == value,
                    onClick = { onCertChange(value) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = MutualTLS.entries.size
                    ),
                    icon = { SegmentedButtonDefaults.CheckIcon(cert == value) }
                ) {
                    Text(text = stringResource(value.text))
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = cert.placeholder) },
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .heightIn(min = 200.dp)
                .padding(contentPadding.horizontal())
                .fillMaxWidth()
        )

        ImportButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(contentPadding.copy(top = 0.dp)),
            onResult = onValueChange
        )
    }
}

@Composable
private fun ImportButton(
    modifier: Modifier = Modifier,
    onResult: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val import = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            context.contentResolver.openInputStream(uri)?.use {
                onResult(it.readBytes().toString(Charsets.UTF_8))
            }
        }
    }

    FilledTonalButton(
        onClick = { import.launch(Const.CERT_MIMETYPE) },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.file_certificate),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

        Text(text = stringResource(R.string.server_from_file))
    }
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    control: Control,
    setControl: (Control) -> Unit,
    onDelete: () -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    if (isEdit) R.string.server_edit_title
                    else R.string.server_add_title
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (control.isNoEdit) {
                        setControl(Control.Edit)
                    } else {
                        if (isImeVisible) keyboardController?.hide()
                        navController.navigateUp()
                    }
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = null
                )
            }
        },
        actions = {
            if (isEdit && control.isEdit) {
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash),
                        contentDescription = null
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ActionButton(
    control: Control,
    setControl: (Control) -> Unit,
    onConnect: () -> Unit,
    onSave: () -> Unit
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    FloatingActionButton(
        onClick = {
            if (isImeVisible) keyboardController?.hide()
            when (control) {
                Control.Edit -> onConnect()
                Control.Closed -> setControl(Control.Edit)
                Control.Connected -> onSave()
                else -> {}
            }
        },
        containerColor = if (control.isNetworkUnavailable)
            MaterialTheme.colorScheme.errorContainer
        else
            FloatingActionButtonDefaults.containerColor
    ) {
        Icon(
            painter = painterResource(
                when (control) {
                    Control.NetworkUnavailable -> R.drawable.cloud_off
                    Control.Edit, Control.Connecting -> R.drawable.plug_connected
                    Control.Closed -> R.drawable.plug_connected_x
                    Control.Connected, Control.Saved -> R.drawable.device_floppy
                }
            ),
            contentDescription = null
        )
    }
}