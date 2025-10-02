package dev.sanmer.whalya.ansi

import androidx.compose.ui.graphics.Color

object ANSIConst {
    const val ESC = '\u001B'
    const val BRACKET = '['
    const val END = 'm'
    const val SPLIT = ';'

    // Visual Studio Code
    val Black = Color(0, 0, 0)
    val Red = Color(205, 49, 49)
    val Green = Color(13, 188, 121)
    val Yellow = Color(229, 229, 16)
    val Blue = Color(36, 114, 200)
    val Magenta = Color(188, 63, 188)
    val Cyan = Color(17, 168, 205)
    val White = Color(229, 229, 229)
    val Gray = Color(102, 102, 102)
    val BrightRed = Color(241, 76, 76)
    val BrightGreen = Color(35, 209, 139)
    val BrightYellow = Color(245, 245, 67)
    val BrightBlue = Color(59, 142, 234)
    val BrightMagenta = Color(214, 112, 214)
    val BrightCyan = Color(41, 184, 219)
    val BrightWhite = Color(229, 229, 229)

}