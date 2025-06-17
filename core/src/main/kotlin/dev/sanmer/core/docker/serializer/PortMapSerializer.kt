package dev.sanmer.core.docker.serializer

import dev.sanmer.core.docker.response.container.ContainerLowLevel.NetworkSettings
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PortMapSerializer : KSerializer<NetworkSettings.PortMap> {
    private val serializer = MapSerializer(
        String.serializer(),
        ListSerializer(NetworkSettings.PortBinding.serializer()).nullable
    )

    override val descriptor = mapSerialDescriptor<String, NetworkSettings.PortBinding>()

    override fun deserialize(decoder: Decoder): NetworkSettings.PortMap {
        return NetworkSettings.PortMap(
            ports = decoder.decodeSerializableValue(serializer)
                .mapValues { it.value.orEmpty() }
        )
    }

    override fun serialize(encoder: Encoder, value: NetworkSettings.PortMap) {
        encoder.encodeSerializableValue(
            serializer,
            value.ports.mapValues {
                it.value.ifEmpty { null }
            }
        )
    }
}