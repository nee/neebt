package dev.nee

import java.io.DataInputStream
import java.io.DataOutputStream

internal class Type<T : Any>(
	val read: DataInputStream.() -> T,
	val write: DataOutputStream.(T) -> Unit,
	val toSNBT: (T) -> String
) {
	companion object {
		const val BYTE = 1.toByte()
		const val SHORT = 2.toByte()
		const val INT = 3.toByte()
		const val LONG = 4.toByte()
		const val FLOAT = 5.toByte()
		const val DOUBLE = 6.toByte()
		const val BYTE_ARRAY = 7.toByte()
		const val STRING = 8.toByte()
		const val LIST = 9.toByte()
		const val COMPOUND = 10.toByte()
		const val INT_ARRAY = 11.toByte()
		const val LONG_ARRAY = 12.toByte()

		@Suppress("UNCHECKED_CAST")
		infix fun forId(id: Byte) = types[id]!! as Type<Any>

		fun idForValue(value: Any?): Byte =
			when (value) {
				is Byte      -> BYTE
				is Short     -> SHORT
				is Int       -> INT
				is Long      -> LONG
				is Float     -> FLOAT
				is Double    -> DOUBLE
				is ByteArray -> BYTE_ARRAY
				is String    -> STRING
				is List<*>   -> LIST
				is Map<*, *> -> COMPOUND
				is IntArray  -> INT_ARRAY
				is LongArray -> LONG_ARRAY
				else         -> throw IllegalArgumentException("Can't use $value in NBT")
			}

		@OptIn(ExperimentalStdlibApi::class)
		@Suppress("RemoveExplicitTypeArguments")
		private val types: Map<Byte, Type<*>> by lazy {
			mapOf(
				BYTE to Type<Byte>(
					{ readByte() },
					{ writeByte(it.toInt()) },
					{ "${it}b" },
				),
				SHORT to Type<Short>(
					{ readShort() },
					{ writeShort(it.toInt()) },
					{ "${it}s" },
				),
				INT to Type<Int>(
					{ readInt() },
					{ writeInt(it) },
					{ "$it" },
				),
				LONG to Type<Long>(
					{ readLong() },
					{ writeLong(it) },
					{ "${it}L" },
				),
				FLOAT to Type<Float>(
					{ readFloat() },
					{ writeFloat(it) },
					{ "$it" },
				),
				DOUBLE to Type<Double>(
					{ readDouble() },
					{ writeDouble(it) },
					{ "${it}D" },
				),
				BYTE_ARRAY to Type<ByteArray>(
					{
						val size = readInt()
						readNBytes(size)
					},
					{
						writeInt(it.size)
						write(it)
					},
					{ "[${it.joinToString(", ") { (Type forId BYTE).toSNBT(it) }}]" },
				),
				STRING to Type<String>(
					{ readUTF() },
					{ writeUTF(it) },
					{ "\"$it\"" },
				),
				LIST to Type<NBTList<Any>>(
					{
						val type = readByte()
						val size = readInt()
						NBTList(type, List(size) { (Type forId type).read(this) })
					},
					{ list ->
						writeByte(list.type.toInt())
						writeInt(list.size)
						list.forEach { (Type forId list.type).write(this, it) }
					},
					{ list ->
						if (list.type in 1..6) "[${list.joinToString(", ") { (Type forId list.type).toSNBT(it) }}]"
						else "[\n${
							list.joinToString(",\n") { (Type forId list.type).toSNBT(it) }.prependIndent("    ")
						}\n]"
					}
				),
				COMPOUND to Type<NBTCompound<Any>>(
					{
						NBTCompound(
							buildMap<Pair<String, Byte>, Any> {
								var type: Byte = readByte()
								while (type != 0.toByte()) {
									val name = readUTF()
									val value = (Type forId type).read(this@Type)
									set(name to type, value)
									type = readByte()
								}
							}
						)
					},
					{
						it.forEach { (name, type), value ->
							writeByte(type.toInt())
							writeUTF(name)
							(Type forId type).write(this, value)
						}
						writeByte(0)
					},
					{ compound ->
						"{\n${
							compound.entries
								.joinToString(",\n") { "\"${it.key.first}\": ${(Type forId it.key.second).toSNBT(it.value)}" }
								.prependIndent("    ")
						}\n}"
					}
				),
				INT_ARRAY to Type<IntArray>(
					{
						val size = readInt()
						IntArray(size) { readInt() }
					},
					{
						writeInt(it.size)
						it.forEach { writeInt(it) }
					},
					{ "[${it.joinToString(", ") { (Type forId INT).toSNBT(it) }}]" },
				),
				LONG_ARRAY to Type<LongArray>(
					{
						val size = readInt()
						LongArray(size) { readLong() }
					},
					{
						writeInt(it.size)
						it.forEach { writeLong(it) }
					},
					{ "[${it.joinToString(", ") { (Type forId LONG).toSNBT(it) }}]" },
				),
			)
		}
	}
}