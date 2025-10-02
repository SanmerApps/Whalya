package dev.sanmer.whalya.ansi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

object ANSI {
    private val colors = listOf(
        ANSIConst.Black, ANSIConst.Red, ANSIConst.Green, ANSIConst.Yellow,
        ANSIConst.Blue, ANSIConst.Magenta, ANSIConst.Cyan, ANSIConst.White,
        ANSIConst.Gray, ANSIConst.BrightRed, ANSIConst.BrightGreen, ANSIConst.BrightYellow,
        ANSIConst.BrightBlue, ANSIConst.BrightMagenta, ANSIConst.BrightCyan, ANSIConst.BrightWhite
    )

    fun Color.Companion.valueOfANSI256(n: Int): Color {
        require(n in 0..255)
        return when {
            n < 16 -> colors[n]
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

    interface Parser {
        fun readChar(): Char?

        private fun readParameters(bufferSize: Int = 3): List<Int> {
            val start = readChar() ?: return emptyList()
            if (start != ANSIConst.BRACKET) return emptyList()
            return buildList {
                val buffer = CharArray(bufferSize)
                var size = 0
                while (true) {
                    when (val char = readChar() ?: break) {
                        ANSIConst.END -> {
                            String(buffer, 0, size)
                                .toIntOrNull()
                                ?.let(::add)
                            break
                        }

                        ANSIConst.SPLIT -> {
                            String(buffer, 0, size)
                                .toIntOrNull()
                                ?.let(::add)
                            size = 0
                        }

                        else -> buffer[size++] = char
                    }
                }
            }
        }

        fun parse() = buildList {
            var style = SpanStyle()
            val buffer = StringBuilder()
            while (true) {
                when (val char = readChar()) {
                    null -> {
                        val value = buffer.toString()
                        if (value.isNotEmpty()) {
                            add(WithStyle(value, style))
                        }
                        break
                    }

                    ANSIConst.ESC -> {
                        val value = buffer.toString()
                        if (value.isNotEmpty()) {
                            add(WithStyle(value, style))
                            buffer.clear()
                        }
                        style = readParameters().toSpanStyle()
                    }

                    else -> buffer.append(char)
                }
            }
        }
    }

    private class StringParser(
        text: String
    ) : Parser {
        private val reader = text.reader()

        override fun readChar(): Char? {
            val value = reader.read()
            return if (value == -1) null else value.toChar()
        }
    }

    fun String.toANSITexts() = try {
        StringParser(this).parse()
    } catch (_: Throwable) {
        listOf(WithStyle(this))
    }

    fun AnnotatedString.Builder.append(text: WithStyle) {
        withStyle(text.style) { append(text.value) }
    }

    fun AnnotatedString.Builder.appendANSI(text: String) {
        text.toANSITexts().forEach { append(it) }
    }

    data class WithStyle(
        val value: String,
        val style: SpanStyle = SpanStyle()
    )
}