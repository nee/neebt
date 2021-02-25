package dev.nee

internal data class NBTList<E : Any>(val type: Byte, val backing: List<E>) : List<E> by backing