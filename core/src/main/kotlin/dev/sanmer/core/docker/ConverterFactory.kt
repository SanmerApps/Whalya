package dev.sanmer.core.docker

import dev.sanmer.core.docker.response.container.ContainerLog
import dev.sanmer.core.ktx.isList
import dev.sanmer.core.ktx.isType
import dev.sanmer.core.ktx.unsigned
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Type

object ConverterFactory : Converter.Factory() {
    fun ContainerLog.Default.from(stream: InputStream, use: (ContainerLog) -> Unit) {
        val header = ByteArray(MULTIPLEXED_BLOCK_SIZE)
        while (stream.read(header) == MULTIPLEXED_BLOCK_SIZE) {
            val type = header[0].unsigned()
            val contentLength = (header[4].unsigned() shl 24) or
                    (header[5].unsigned() shl 16) or
                    (header[6].unsigned() shl 8) or
                    header[7].unsigned()

            if (contentLength == 0) continue
            val content = ByteArray(contentLength)
            stream.read(content, 0, contentLength)

            val text = content.toString(Charsets.UTF_8)
            if (text.any { it.isLetterOrDigit() }) {
                use(
                    ContainerLog(
                        type = ContainerLog.Type.entries[type],
                        content = text.trim { it.isWhitespace() || it == '\n' }
                    )
                )
            }
        }

        stream.close()
    }

    fun ContainerLog.Default.from(reader: Reader, use: (ContainerLog) -> Unit) {
        return reader.buffered().readLines()
            .forEach { text ->
                if (text.any { it.isLetterOrDigit() }) {
                    use(
                        ContainerLog(
                            type = ContainerLog.Type.Stdout,
                            content = text
                        )
                    )
                }
            }
    }

    private fun ContainerLog.Default.from(body: ResponseBody): List<ContainerLog> {
        val contentType = body.contentType().toString()
        val stream = body.byteStream()
        return buildList {
            when (contentType) {
                RAW -> from(stream.reader(), ::add)
                MULTIPLEXED -> from(stream.buffered(), ::add)
                else -> throw IllegalStateException("Content-Type = $contentType")
            }
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return when {
            type.isType<String>() -> Converter { it.string() }
            type.isList<ContainerLog>() -> Converter { ContainerLog.from(it) }
            else -> null
        }
    }
}