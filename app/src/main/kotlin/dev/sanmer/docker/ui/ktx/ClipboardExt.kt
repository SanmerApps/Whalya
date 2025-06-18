package dev.sanmer.docker.ui.ktx

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry

suspend fun Clipboard.setText(
    content: String
) {
    val data = ClipData.newPlainText("plain text", content)
    setClipEntry(data.toClipEntry())
}