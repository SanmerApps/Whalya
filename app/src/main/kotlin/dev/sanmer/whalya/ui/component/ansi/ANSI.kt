package dev.sanmer.whalya.ui.component.ansi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import java.io.StringReader

private val baseColors = arrayOf(
    ANSIConst.Black, ANSIConst.Red, ANSIConst.Green, ANSIConst.Yellow,
    ANSIConst.Blue, ANSIConst.Magenta, ANSIConst.Cyan, ANSIConst.White,
    ANSIConst.Gray, ANSIConst.BrightRed, ANSIConst.BrightGreen, ANSIConst.BrightYellow,
    ANSIConst.BrightBlue, ANSIConst.BrightMagenta, ANSIConst.BrightCyan, ANSIConst.BrightWhite
)

private fun Color.Companion.valueOfANSI256(n: Int): Color {
    require(n in 0..255)
    return when {
        n < 16 -> baseColors[n]
        n < 232 -> {
            val i = n - 16
            val red = i / 36
            val green = (i % 36) / 6
            val blue = i % 6
            val r = if (red != 0) red * 40 + 55 else 0
            val g = if (green != 0) green * 40 + 55 else 0
            val b = if (blue != 0) blue * 40 + 55 else 0
            Color(r, g, b)
        }

        else -> {
            val gray = (n - 232)
            val level = gray * 10 + 8
            Color(level, level, level)
        }
    }
}

private fun StringReader.readChar(): Char? {
    val value = read()
    return if (value == -1) null else value.toChar()
}

private fun StringReader.readParameters(): List<Int> {
    val start = readChar() ?: return emptyList()
    if (start != ANSIConst.BRACKET) return emptyList()
    return buildList {
        val builder = StringBuilder()
        fun addSafety() {
            val value = builder.toString()
            value.toIntOrNull()?.let(::add)
            builder.clear()
        }
        while (true) {
            val char = readChar() ?: break
            if (char == ANSIConst.END) {
                addSafety()
                break
            }
            if (char == ANSIConst.SPLIT) {
                addSafety()
            } else {
                builder.append(char)
            }
        }
    }
}

private fun ListIterator<Int>.readColor(): Color {
    return when (next()) {
        5 -> Color.valueOfANSI256(next())
        2 -> Color(next(), next(), next())
        else -> Color.Unspecified
    }
}

private fun List<Int>.toSpanStyle(): SpanStyle {
    var style = SpanStyle()
    val iterator = listIterator()
    while (iterator.hasNext()) {
        style = when (ANSIParameter.entries[iterator.next()]) {
            ANSIParameter.Normal -> break
            ANSIParameter.Bold -> style.copy(fontWeight = FontWeight.Bold)
            ANSIParameter.Italic -> style.copy(fontStyle = FontStyle.Italic)
            ANSIParameter.Underline -> style.copy(textDecoration = TextDecoration.Underline)
            ANSIParameter.Strikethrough -> style.copy(textDecoration = TextDecoration.LineThrough)
            ANSIParameter.ForegroundBlack -> style.copy(color = ANSIConst.Black)
            ANSIParameter.ForegroundRed -> style.copy(color = ANSIConst.Red)
            ANSIParameter.ForegroundGreen -> style.copy(color = ANSIConst.Green)
            ANSIParameter.ForegroundYellow -> style.copy(color = ANSIConst.Yellow)
            ANSIParameter.ForegroundBlue -> style.copy(color = ANSIConst.Blue)
            ANSIParameter.ForegroundMagenta -> style.copy(color = ANSIConst.Magenta)
            ANSIParameter.ForegroundCyan -> style.copy(color = ANSIConst.Cyan)
            ANSIParameter.ForegroundWhite -> style.copy(color = ANSIConst.White)
            ANSIParameter.ForegroundCustom -> style.copy(color = iterator.readColor())
            ANSIParameter.ForegroundDefault -> style.copy(color = Color.Unspecified)
            ANSIParameter.BackgroundBlack -> style.copy(background = ANSIConst.Black)
            ANSIParameter.BackgroundRed -> style.copy(background = ANSIConst.Red)
            ANSIParameter.BackgroundGreen -> style.copy(background = ANSIConst.Green)
            ANSIParameter.BackgroundYellow -> style.copy(background = ANSIConst.Yellow)
            ANSIParameter.BackgroundBlue -> style.copy(background = ANSIConst.Blue)
            ANSIParameter.BackgroundMagenta -> style.copy(background = ANSIConst.Magenta)
            ANSIParameter.BackgroundCyan -> style.copy(background = ANSIConst.Cyan)
            ANSIParameter.BackgroundWhite -> style.copy(background = ANSIConst.White)
            ANSIParameter.BackgroundCustom -> style.copy(background = iterator.readColor())
            ANSIParameter.BackgroundDefault -> style.copy(background = Color.Unspecified)
            ANSIParameter.ForegroundGray -> style.copy(color = ANSIConst.Gray)
            ANSIParameter.ForegroundBrightRed -> style.copy(color = ANSIConst.BrightRed)
            ANSIParameter.ForegroundBrightGreen -> style.copy(color = ANSIConst.BrightGreen)
            ANSIParameter.ForegroundBrightYellow -> style.copy(color = ANSIConst.BrightYellow)
            ANSIParameter.ForegroundBrightBlue -> style.copy(color = ANSIConst.BrightBlue)
            ANSIParameter.ForegroundBrightMagenta -> style.copy(color = ANSIConst.BrightMagenta)
            ANSIParameter.ForegroundBrightCyan -> style.copy(color = ANSIConst.BrightCyan)
            ANSIParameter.ForegroundBrightWhite -> style.copy(color = ANSIConst.BrightWhite)
            ANSIParameter.BackgroundGray -> style.copy(background = ANSIConst.Gray)
            ANSIParameter.BackgroundBrightRed -> style.copy(background = ANSIConst.BrightRed)
            ANSIParameter.BackgroundBrightGreen -> style.copy(background = ANSIConst.BrightGreen)
            ANSIParameter.BackgroundBrightYellow -> style.copy(background = ANSIConst.BrightYellow)
            ANSIParameter.BackgroundBrightBlue -> style.copy(background = ANSIConst.BrightBlue)
            ANSIParameter.BackgroundBrightMagenta -> style.copy(background = ANSIConst.BrightMagenta)
            ANSIParameter.BackgroundBrightCyan -> style.copy(background = ANSIConst.BrightCyan)
            ANSIParameter.BackgroundBrightWhite -> style.copy(background = ANSIConst.BrightWhite)
            else -> continue
        }
    }
    return style
}

private fun StringReader.parseANSI(): List<Pair<SpanStyle, String>> {
    return buildList {
        var style = SpanStyle()
        val builder = StringBuilder()
        fun addNotEmpty() {
            val text = builder.toString()
            if (builder.isNotEmpty()) add(style to text)
            builder.clear()
        }
        while (true) {
            val char = readChar()
            if (char == null) {
                addNotEmpty()
                break
            }
            if (char == ANSIConst.ESC) {
                addNotEmpty()
                style = readParameters().toSpanStyle()
            } else {
                builder.append(char)
            }
        }
    }
}

private fun String.parseANSIOrNull() = try {
    reader().parseANSI()
} catch (e: Throwable) {
    null
}

fun AnnotatedString.Builder.appendANSIOrDefault(text: String) {
    text.parseANSIOrNull()?.forEach {
        withStyle(it.first) { append(it.second) }
    } ?: append(text)
}