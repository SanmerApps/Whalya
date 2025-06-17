package dev.sanmer.docker.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ValuesFlow(
    title: String,
    values: Collection<String>,
    modifier: Modifier = Modifier
) {
    ValuesFlowRow(
        title = title,
        modifier = modifier
    ) {
        values.forEach {
            LabelText(
                text = it
            )
        }
    }
}