package dev.sanmer.core.ktx

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

inline fun <reified T> Type.isType() = this == T::class.java

inline fun <reified E> Type.isList() = this is ParameterizedType &&
        rawType == List::class.java &&
        actualTypeArguments[0].isType<E>()