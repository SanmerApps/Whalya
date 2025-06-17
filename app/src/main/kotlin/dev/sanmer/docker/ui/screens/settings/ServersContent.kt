package dev.sanmer.docker.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.docker.R
import dev.sanmer.docker.database.entity.ServerEntity
import dev.sanmer.docker.ui.component.ValueText
import dev.sanmer.docker.ui.ktx.navigateSingleTopTo
import dev.sanmer.docker.ui.main.Screen

@Composable
fun ServersContent(
    navController: NavController,
    servers: List<ServerEntity>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                border = CardDefaults.outlinedCardBorder(),
                shape = MaterialTheme.shapes.large
            )
            .clip(shape = MaterialTheme.shapes.large)
    ) {
        servers.forEach { server ->
            ServerItem(
                server = server
            ) {
                navController.navigateSingleTopTo(Screen.AddServer(server.id))
            }

            HorizontalDivider()
        }

        AddServerItem {
            navController.navigateSingleTopTo(Screen.AddServer())
        }
    }
}

@Composable
private fun ServerItem(
    server: ServerEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                enabled = true
            )
            .padding(all = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ValueText(
            title = server.name,
            value = server.baseUrl,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun AddServerItem(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                enabled = true
            )
            .padding(all = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.server_add_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}