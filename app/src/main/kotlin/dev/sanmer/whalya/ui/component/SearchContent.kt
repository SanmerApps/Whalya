package dev.sanmer.whalya.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.sanmer.whalya.R

@Composable
fun SearchContent(
    onSearch: (String) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current
) {
    var key by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(true) {
        focusRequester.requestFocus()
        keyboardController?.show()
        onDispose {
            key = ""
            keyboardController?.hide()
        }
    }

    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = null
        )

        BasicTextField(
            value = key,
            onValueChange = {
                onSearch(it)
                key = it
            },
            textStyle = textStyle,
            modifier = Modifier
                .defaultMinSize(
                    minWidth = OutlinedTextFieldDefaults.MinWidth
                )
                .focusRequester(focusRequester)
        )
    }
}