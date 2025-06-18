package dev.sanmer.docker.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanmer.docker.R
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.ui.ktx.bottom
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun OperationResultBottomSheet(
    onDismiss: () -> Unit,
    data: LoadData<*>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(data) {
        if (data.isSuccess) {
            delay(500.milliseconds)
            onDismiss()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp)
    ) {
        when (data) {
            LoadData.Pending, LoadData.Loading -> Loading(
                height = 200.dp
            )

            is LoadData.Success<*> -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.circle_check),
                    contentDescription = null,
                    modifier = Modifier.size(PageIndicatorDefaults.IconSize),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            is LoadData.Failure -> Failed(
                error = data.error,
                height = 200.dp
            )
        }
    }
}