package dev.sanmer.core

import dev.sanmer.core.ktx.unsigned
import dev.sanmer.core.response.container.ContainerLog
import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.http.ContentType
import io.ktor.serialization.ContentConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.exhausted
import io.ktor.utils.io.readByteArray
import io.ktor.utils.io.readUTF8Line
import kotlin.reflect.typeOf

internal object ConverterFactory {
    suspend fun ContainerLog.Default.fromMultiplexed(
        channel: ByteReadChannel,
        use: (ContainerLog) -> Unit
    ) {
        while (!channel.exhausted()) {
            val header = channel.readByteArray(MULTIPLEXED_BLOCK_SIZE)
            val type = header[0].unsigned()
            val contentLength = (header[4].unsigned() shl 24) or
                    (header[5].unsigned() shl 16) or
                    (header[6].unsigned() shl 8) or
                    header[7].unsigned()

            if (contentLength == 0) continue
            val content = channel.readByteArray(contentLength)

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
    }

    suspend fun ContainerLog.Default.fromRaw(
        channel: ByteReadChannel,
        use: (ContainerLog) -> Unit
    ) {
        while (!channel.exhausted()) {
            val text = channel.readUTF8Line() ?: continue
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

    fun ContentNegotiationConfig.containerLog(
        contentType: ContentType
    ) = register(
        contentType = contentType,
        converter = object : ContentConverter {
            override suspend fun deserialize(
                charset: Charset,
                typeInfo: TypeInfo,
                content: ByteReadChannel
            ) = if (typeInfo.kotlinType == typeOf<List<ContainerLog>>()) buildList<ContainerLog> {
                when (contentType) {
                    ContainerLog.RAW -> ContainerLog.fromRaw(content, ::add)
                    ContainerLog.MULTIPLEXED -> ContainerLog.fromMultiplexed(content, ::add)
                }
            } else null

            override suspend fun serialize(
                contentType: ContentType,
                charset: Charset,
                typeInfo: TypeInfo,
                value: Any?
            ) = null

        }
    )
}