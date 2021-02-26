package dev.nee

import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * A wrapper for public NBT functions.
 */
@Suppress("UNCHECKED_CAST", "unused")
object NBT {
	/**
	 * Converts any value to its NBT counterpart:
	 * Primitives and Arrays stay unchanged,
	 * lists and maps are converted to NBTLists and NBTCompounds, respectively.
	 * This function assumes that the receiver is valid (map keys are strings, the type exists in NBT).
	 */
	private fun Any?.toNBT(): Any {
		return when (this) {
			is Byte,
			is Short,
			is Int,
			is Long,
			is Float,
			is Double,
			is ByteArray,
			is String,
			is IntArray,
			is LongArray -> this
			is List<*>   -> {
				val type = Type.idForValue(first())
				forEach {
					require(Type.idForValue(it) == type) { "$it is not of the same type as ${first()} in NBT list" }
				}
				NBTList(type, map { it.toNBT() })
			}
			is Map<*, *> -> {
				keys.forEach {
					require(it is String) { "$it is not a string and therefore can't be used as a key in a NBT compound" }
				}
				NBTCompound(mapKeys { (it.key as String) to Type.idForValue(it.value) }.mapValues { it.value.toNBT() })
			}
			else         -> throw IllegalArgumentException(
				"Can't use $this in NBT"
			)
		}
	}

	/**
	 * Converts any NBT value to its non-NBT counterpart:
	 * Primitives and Arrays stay unchanged,
	 * NBTLists and NBTCompounds are converted to lists and maps, respectively.
	 * This function assumes that the receiver is valid (the type exists in NBT).
	 */
	private fun Any.toNonNBT(): Any {
		return when (this) {
			is Byte,
			is Short,
			is Int,
			is Long,
			is Float,
			is Double,
			is ByteArray,
			is String,
			is IntArray,
			is LongArray      -> this
			is NBTList<*>     -> this.map { it.toNonNBT() }
			is NBTCompound<*> -> this.mapKeys { it.key.first }.mapValues { it.value.toNonNBT() }
			else              -> throw IllegalArgumentException(
				"Can't convert $this to non-NBT (should never happen)"
			)
		}
	}

	fun DataOutputStream.writeNBT(byte: Byte) = (Type forId Type.BYTE).write(this, byte)
	fun DataOutputStream.writeNBT(short: Short) = (Type forId Type.SHORT).write(this, short)
	fun DataOutputStream.writeNBT(int: Int) = (Type forId Type.INT).write(this, int)
	fun DataOutputStream.writeNBT(long: Long) = (Type forId Type.LONG).write(this, long)
	fun DataOutputStream.writeNBT(float: Float) = (Type forId Type.FLOAT).write(this, float)
	fun DataOutputStream.writeNBT(double: Double) = (Type forId Type.DOUBLE).write(this, double)
	fun DataOutputStream.writeNBT(byteArray: ByteArray) = (Type forId Type.BYTE_ARRAY).write(this, byteArray)
	fun DataOutputStream.writeNBT(string: String) = (Type forId Type.STRING).write(this, string)
	fun DataOutputStream.writeNBT(intArray: IntArray) = (Type forId Type.INT_ARRAY).write(this, intArray)
	fun DataOutputStream.writeNBT(longArray: LongArray) = (Type forId Type.LONG_ARRAY).write(this, longArray)
	fun <E> DataOutputStream.writeNBT(list: List<E>) = (Type forId Type.LIST).write(this, list.toNBT())
	fun <V> DataOutputStream.writeNBT(map: Map<String, V>) = (Type forId Type.COMPOUND).write(this, map.toNBT())
	fun <V> DataOutputStream.writeNamedNBTCompound(name: String, map: Map<String, V>) {
		writeByte(Type.COMPOUND.toInt())
		writeUTF(name)
		writeNBT(map)
	}

	fun DataInputStream.readNBTByte(): Byte = (Type forId Type.BYTE).read(this) as Byte
	fun DataInputStream.readNBTShort(): Short = (Type forId Type.SHORT).read(this) as Short
	fun DataInputStream.readNBTInt(): Int = (Type forId Type.INT).read(this) as Int
	fun DataInputStream.readNBTLong(): Long = (Type forId Type.LONG).read(this) as Long
	fun DataInputStream.readNBTFloat(): Float = (Type forId Type.FLOAT).read(this) as Float
	fun DataInputStream.readNBTDouble(): Double = (Type forId Type.DOUBLE).read(this) as Double
	fun DataInputStream.readNBTByteArray(): ByteArray = (Type forId Type.BYTE_ARRAY).read(this) as ByteArray
	fun DataInputStream.readNBTString(): String = (Type forId Type.STRING).read(this) as String
	fun DataInputStream.readNBTIntArray(): IntArray = (Type forId Type.INT_ARRAY).read(this) as IntArray
	fun DataInputStream.readNBTLongArray(): LongArray = (Type forId Type.LONG_ARRAY).read(this) as LongArray
	fun DataInputStream.readNBTList(): List<Any> = (Type forId Type.LIST).read(this).toNonNBT() as List<Any>
	fun DataInputStream.readNBTCompound(): Map<String, Any> =
		(Type forId Type.COMPOUND).read(this).toNonNBT() as Map<String, Any>

	fun DataInputStream.readNamedNBTCompound(): Pair<String, Map<String, Any>> {
		check(readByte() == Type.COMPOUND)
		val name = readUTF()
		val compound = readNBTCompound()
		return name to compound
	}

	fun Byte.toSNBT() = (Type forId Type.BYTE).toSNBT(this)
	fun Short.toSNBT() = (Type forId Type.SHORT).toSNBT(this)
	fun Int.toSNBT() = (Type forId Type.INT).toSNBT(this)
	fun Long.toSNBT() = (Type forId Type.LONG).toSNBT(this)
	fun Float.toSNBT() = (Type forId Type.FLOAT).toSNBT(this)
	fun Double.toSNBT() = (Type forId Type.DOUBLE).toSNBT(this)
	fun ByteArray.toSNBT() = (Type forId Type.BYTE_ARRAY).toSNBT(this)
	fun String.toSNBT() = (Type forId Type.STRING).toSNBT(this)
	fun IntArray.toSNBT() = (Type forId Type.INT_ARRAY).toSNBT(this)
	fun LongArray.toSNBT() = (Type forId Type.LONG_ARRAY).toSNBT(this)
	fun <E> List<E>.toSNBT() = (Type forId Type.LIST).toSNBT(this.toNBT())
	fun <V> Map<String, V>.toSNBT() = (Type forId Type.COMPOUND).toSNBT(this.toNBT())

	/**
	 * Merges [a] and [b] deeply with the following algorithm:
	 * ```
	 * c = a
	 * for (key in b) {
	 *     if (key in a && a[key] is Map && b[key] is Map) {
	 *         c[key] = mergeMaps(a[key], b[key])
	 *     } else {
	 *         c[key] = b[key]
	 *     }
	 * }
	 * ```
	 *
	 * @return `c`, the merged map
	 */
	fun mergeMaps(a: Map<String, Any>, b: Map<String, Any>): Map<String, Any> {
		val c = a.toMutableMap()
		b.forEach { (key, value) ->
			if (key in c && value is Map<*, *> && c[key] is Map<*, *>) {
				c[key] = mergeMaps(c[key] as Map<String, Any>, value as Map<String, Any>)
			} else c[key] = value
		}
		return c
	}

	/**
	 * Merges [a] and [b] deeply with the following algorithm:
	 * ```
	 * c = a
	 * for (index in b) {
	 *     if (index in a && a[index] is List && b[index] is List) {
	 *         c[index] = mergeLists(a[index], b[index])
	 *     } else {
	 *         c[index] = b[index]
	 *     }
	 * }
	 * ```
	 *
	 * @return `c`, the merged list
	 */
	fun mergeLists(a: List<Any>, b: List<Any>): List<Any> {
		val c = a.toMutableList()
		b.forEachIndexed { index, value ->
			if (index in c.indices) {
				if (value is List<*> && c[index] is List<*>) {
					c[index] = mergeLists(c[index] as List<Any>, value as List<Any>)
				} else c[index] = value
			} else c.add(value)
		}
		return c
	}

	/**
	 * Merges [a] and [b] deeply with the following algorithm:
	 * ```
	 * c = a
	 * for (key in b) {
	 *     if (key in a) {
	 *         if (a[key] is Map && b[key] is Map) {
	 *             c[key] = merge(a[key], b[key])
	 *         }
	 *         else if (a[key] is List && b[key] is List) {
	 *             c[key] = merge(a[key], b[key])
	 *         }
	 *         else {
	 *             c[key] = b[key]
	 *         }
	 *     } else {
	 *         c[key] = b[key]
	 *     }
	 * }
	 * ```
	 *
	 * @return `c`, the merged map
	 */
	fun merge(a: Map<String, Any>, b: Map<String, Any>): Map<String, Any> {
		val c = a.toMutableMap()
		b.forEach { (key, value) ->
			if (key in c) {
				val valueBefore = c[key]
				if (value is Map<*, *> && valueBefore is Map<*, *>) {
					c[key] = merge(valueBefore as Map<String, Any>, value as Map<String, Any>)
				} else if (value is List<*> && valueBefore is List<*>) {
					c[key] = merge(valueBefore as List<Any>, value as List<Any>)
				} else c[key] = value
			} else c[key] = value
		}
		return c
	}

	/**
	 * Merges [a] and [b] deeply with the following algorithm:
	 * ```
	 * c = a
	 * for (key in b) {
	 *     if (key in a) {
	 *         if (a[key] is Map && b[key] is Map) {
	 *             c[key] = merge(a[key], b[key])
	 *         }
	 *         else if (a[key] is List && b[key] is List) {
	 *             c[key] = a[key] + b[key]
	 *         }
	 *         else {
	 *             c[key] = b[key]
	 *         }
	 *     } else {
	 *         c[key] = b[key]
	 *     }
	 * }
	 * ```
	 *
	 * @return `c`, the merged map
	 */
	fun mergeAdding(a: Map<String, Any>, b: Map<String, Any>): Map<String, Any> {
		val c = a.toMutableMap()
		b.forEach { (key, value) ->
			if (key in c) {
				val valueBefore = c[key]
				if (value is Map<*, *> && valueBefore is Map<*, *>) {
					c[key] = merge(valueBefore as Map<String, Any>, value as Map<String, Any>)
				} else if (value is List<*> && valueBefore is List<*>) {
					c[key] = valueBefore + value
				} else c[key] = value
			} else c[key] = value
		}
		return c
	}

	/**
	 * Merges [a] and [b] deeply with the following algorithm:
	 * ```
	 * c = a
	 * for (index in b) {
	 *     if (index in a) {
	 *         if (a[index] is Map && b[index] is Map) {
	 *             c[index] = merge(a[index], b[index])
	 *         }
	 *         else if (a[index] is List && b[index] is List) {
	 *             c[index] = merge(a[index], b[index])
	 *         }
	 *         else {
	 *             c[index] = b[index]
	 *         }
	 *     } else {
	 *         c[index] = b[index]
	 *     }
	 * }
	 * ```
	 *
	 * @return `c`, the merged list
	 */
	fun merge(a: List<Any>, b: List<Any>): List<Any> {
		val c = a.toMutableList()
		b.forEachIndexed { index, value ->
			if (index in c.indices) {
				val valueBefore = c[index]
				if (value is List<*> && valueBefore is List<*>) {
					c[index] = merge(valueBefore as List<Any>, value as List<Any>)
				} else if (value is Map<*, *> && valueBefore is Map<*, *>) {
					c[index] = merge(valueBefore as Map<String, Any>, value as Map<String, Any>)
				} else c[index] = value
			} else c.add(value)
		}
		return c
	}
}