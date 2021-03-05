package dev.nee

import dev.nee.NBT.writeNBT
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.test.assertEquals

internal class NBTTest {
	private var outputStream = ByteArrayOutputStream()
	private var dataOutputStream = DataOutputStream(outputStream)

	private fun resetOutput() {
		outputStream = ByteArrayOutputStream()
		dataOutputStream = DataOutputStream(outputStream)
	}

	@Test
	fun writeNBTByte() {
		repeat(Byte.MAX_VALUE - Byte.MIN_VALUE) {
			resetOutput()
			val byte = (it + Byte.MIN_VALUE).toByte()
			dataOutputStream.writeNBT(byte)
			assertEquals(outputStream.toByteArray().single(), byte)
		}
	}

	@Test
	fun writeNBTShort() {
		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			resetOutput()
			val short = (it + Short.MIN_VALUE).toShort()
			dataOutputStream.writeNBT(short)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, 2)
			assertEquals(byteArray.first(), (short.toInt() shr 8).toByte())
			assertEquals(byteArray.last(), short.toByte())
		}
	}

	@Test
	fun writeNBTInt() {
		fun test(int: Int) {
			resetOutput()
			dataOutputStream.writeNBT(int)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, 4)
			assertEquals(ByteBuffer.wrap(byteArray).int, int)
		}

		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			test(Random.nextInt())
		}

		test(Int.MIN_VALUE)
		test(0)
		test(Int.MAX_VALUE)
	}

	@Test
	fun writeNBTLong() {
		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			resetOutput()
			val long = Random.nextLong()
			dataOutputStream.writeNBT(long)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, 8)
			assertEquals(ByteBuffer.wrap(byteArray).long, long)
		}
	}

	@Test
	fun writeNBTFloat() {
		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			resetOutput()
			val float = Random.nextFloat()
			dataOutputStream.writeNBT(float)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, 4)
			assertEquals(ByteBuffer.wrap(byteArray).float, float)
		}
	}

	@Test
	fun writeNBTDouble() {
		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			resetOutput()
			val double = Random.nextDouble()
			dataOutputStream.writeNBT(double)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, 8)
			assertEquals(ByteBuffer.wrap(byteArray).double, double)
		}
	}

	@Test
	fun writeNBTByteArray() {
		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			resetOutput()
			val size = it / 16 + 1
			val randomByteArray = Random.nextBytes(size)
			dataOutputStream.writeNBT(randomByteArray)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, size + 4)
			assertEquals(ByteBuffer.wrap(byteArray.take(4).toByteArray()).int, size)
//			assertEquals(byteArray.drop(4).toByteArray(), randomByteArray)
			assert(byteArray.drop(4).toByteArray() contentEquals randomByteArray)
		}
	}

	@Test
	fun writeNBTString() {
		repeat(Short.MAX_VALUE - Short.MIN_VALUE) {
			resetOutput()
			val size = it / 16 + 1
			val randomByteArray = Random.nextBytes(size)
			dataOutputStream.writeNBT(randomByteArray)
			val byteArray = outputStream.toByteArray()
			assertEquals(byteArray.size, size + 4)
			assertEquals(ByteBuffer.wrap(byteArray.take(4).toByteArray()).int, size)
//			assertEquals(byteArray.drop(4).toByteArray(), randomByteArray)
			assert(byteArray.drop(4).toByteArray() contentEquals randomByteArray)
		}
	}

	@Test
	fun writeNBTIntArray() {
	}

	@Test
	fun writeNBTLongArray() {
	}

	@Test
	fun writeNBTList() {
	}

	@Test
	fun writeNamedNBTCompound() {
	}

	@Test
	fun readNBTByte() {
	}

	@Test
	fun readNBTShort() {
	}

	@Test
	fun readNBTInt() {
	}

	@Test
	fun readNBTLong() {
	}

	@Test
	fun readNBTFloat() {
	}

	@Test
	fun readNBTDouble() {
	}

	@Test
	fun readNBTByteArray() {
	}

	@Test
	fun readNBTString() {
	}

	@Test
	fun readNBTIntArray() {
	}

	@Test
	fun readNBTLongArray() {
	}

	@Test
	fun readNBTList() {
	}

	@Test
	fun readNBTCompound() {
	}

	@Test
	fun readNamedNBTCompound() {
	}

	@Test
	fun toSNBTByte() {
	}

	@Test
	fun toSNBTShort() {
	}

	@Test
	fun toSNBTInt() {
	}

	@Test
	fun toSNBTLong() {
	}

	@Test
	fun toSNBTFloat() {
	}

	@Test
	fun toSNBTDouble() {
	}

	@Test
	fun toSNBTByteArray() {
	}

	@Test
	fun toSNBTString() {
	}

	@Test
	fun toSNBTIntArray() {
	}

	@Test
	fun toSNBTLongArray() {
	}

	@Test
	fun toSNBTList() {
	}

	@Test
	fun mergeMapsOnly() {
	}

	@Test
	fun mergeListsOnly() {
	}

	@Test
	fun mergeMaps() {
	}

	@Test
	fun mergeMapsAdding() {
	}

	@Test
	fun mergeLists() {
	}
}