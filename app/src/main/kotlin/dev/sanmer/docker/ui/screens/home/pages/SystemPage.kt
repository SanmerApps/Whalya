package dev.sanmer.docker.ui.screens.home.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.ui.home.UiSystem
import dev.sanmer.docker.ui.component.Failed
import dev.sanmer.docker.ui.component.Loading
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.component.ValuesColumn
import dev.sanmer.docker.ui.component.ValuesFlow
import dev.sanmer.docker.ui.component.WithIcon

@Composable
fun SystemPage(
    data: LoadData<UiSystem>,
    onPrune: () -> Unit
) {
    when (data) {
        LoadData.Pending, LoadData.Loading -> Loading()
        is LoadData.Success<UiSystem> -> SystemContent(
            system = data.value,
            onPrune = onPrune
        )

        is LoadData.Failure -> Failed(data.error)
    }
}

@Composable
private fun SystemContent(
    system: UiSystem,
    onPrune: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OSCard(
            system = system
        )

        DockerCard(
            system = system,
            onPrune = onPrune
        )
    }
}

@Composable
private fun OSCard(
    system: UiSystem
) {
    ValuesColumn {
        WithIcon(
            painter = painterResource(R.drawable.server_2)
        ) {
            ValueText(
                title = stringResource(R.string.os_system),
                value = system.operatingSystem,
                modifier = Modifier.weight(1f)
            )
        }

        ValueText(
            title = stringResource(R.string.os_kernel),
            value = system.kernelVersion
        )

        ValuesFlow(
            title = stringResource(R.string.os_platform),
            values = system.platform
        )
    }
}

@Composable
private fun DockerCard(
    system: UiSystem,
    onPrune: () -> Unit
) {
    ValuesColumn {
        WithIcon(
            painter = painterResource(R.drawable.brand_docker)
        ) {
            ValueText(
                title = stringResource(R.string.driver),
                value = system.driver,
                modifier = Modifier.weight(1f)
            )
        }

        ValueText(
            title = stringResource(R.string.docker_root_dir),
            value = system.dockerRootDir
        )

        ValueText(
            title = stringResource(R.string.docker_api),
            value = system.apiVersion
        )

        ValueText(
            title = stringResource(R.string.docker_go_version),
            value = system.goVersion
        )

        ValueText(
            title = stringResource(R.string.docker_build_time),
            value = system.buildTime
        )

        ValuesFlow(
            title = stringResource(R.string.docker_components),
            values = system.components
        )

        Buttons(
            onPrune = onPrune,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun Buttons(
    onPrune: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalIconButton(
            onClick = onPrune
        ) {
            Icon(
                painter = painterResource(R.drawable.clear_all),
                contentDescription = null
            )
        }
    }
}