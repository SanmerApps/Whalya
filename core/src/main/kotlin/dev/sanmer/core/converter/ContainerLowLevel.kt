package dev.sanmer.core.converter

import dev.sanmer.core.request.container.ContainerConfig
import dev.sanmer.core.response.container.ContainerLowLevel

fun ContainerLowLevel.toConfig() = ContainerConfig(
    hostname = config.hostname,
    domainName = config.domainName,
    user = config.user,
    attachStdin = config.attachStdin,
    attachStdout = config.attachStdout,
    attachStderr = config.attachStderr,
    exposedPorts = config.exposedPorts,
    tty = config.tty,
    openStdin = config.openStdin,
    stdinOnce = config.stdinOnce,
    env = config.env,
    cmd = config.cmd,
    healthCheck = config.healthCheck,
    argsEscaped = config.argsEscaped,
    image = config.image,
    volumes = config.volumes,
    workingDir = config.workingDir,
    entryPoint = config.entryPoint,
    networkDisabled = config.networkDisabled,
    onBuild = config.onBuild,
    labels = config.labels,
    stopSignal = config.stopSignal,
    stopTimeout = config.stopTimeout,
    shell = config.shell,
    hostConfig = hostConfig,
    networkingConfig = ContainerConfig.Networking(
        endpointsConfig = networkSettings.networks.mapValues {
            it.value.copy(macAddress = "")
        }
    )
)