package dev.nee

internal data class NBTCompound<V : Any>(val backing: Map<Pair<String, Byte>, V>) : Map<Pair<String, Byte>, V> by backing