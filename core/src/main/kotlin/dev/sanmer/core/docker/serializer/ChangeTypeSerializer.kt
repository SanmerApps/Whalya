package dev.sanmer.core.docker.serializer

import dev.sanmer.core.docker.response.container.ContainerChanges
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ChangeTypeSerializer : KSerializer<ContainerChanges.ChangeType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "dev.sanmer.core.docker.response.container.FilesystemChanges.ChangeType",
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: ContainerChanges.ChangeType) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): ContainerChanges.ChangeType {
        return ContainerChanges.ChangeType.entries[decoder.decodeInt()]
    }
}
