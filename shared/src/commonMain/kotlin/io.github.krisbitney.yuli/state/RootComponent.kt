package io.github.krisbitney.yuli.state

import com.arkivanov.decompose.ComponentContext

interface YuliRoot {

}

class YuliRootComponent(
    val componentContext: ComponentContext
) : YuliRoot, ComponentContext by componentContext {

}