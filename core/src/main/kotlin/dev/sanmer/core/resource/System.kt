package dev.sanmer.core.resource

import io.ktor.resources.Resource

@Resource("system")
class System {
    @Resource("info")
    class Info

    @Resource("version")
    class Version
}