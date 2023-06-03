package com.mmoczkowski.mavlink.processor

import com.mmoczkowski.mavlink.processor.definition.parseType
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import org.junit.Test

class ParseTypeTest {

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `type parsed properly`() {
        assertTypeParsed("char", Char::class, null)
        assertTypeParsed("float", Float::class, null)
        assertTypeParsed("double", Double::class, null)
        assertTypeParsed("int8_t", Byte::class, null)
        assertTypeParsed("uint8_t", UByte::class, null)
        assertTypeParsed("uint8_t_mavlink_version", UByte::class, null)
        assertTypeParsed("int16_t", Short::class, null)
        assertTypeParsed("uint16_t", UShort::class, null)
        assertTypeParsed("int32_t", Int::class, null)
        assertTypeParsed("uint32_t", UInt::class, null)
        assertTypeParsed("char[2]", CharArray::class, 2)
        assertTypeParsed("float[1]", FloatArray::class, 1)
        assertTypeParsed("double[3]", DoubleArray::class, 3)
        assertTypeParsed("int8_t[7]", ByteArray::class, 7)
        assertTypeParsed("uint8_t[2]", UByteArray::class, 2)
        assertTypeParsed("int16_t[3]", ShortArray::class, 3)
        assertTypeParsed("uint16_t[43]", UShortArray::class, 43)
        assertTypeParsed("int32_t[6]", IntArray::class, 6)
        assertTypeParsed("uint32_t[16]", UIntArray::class, 16)
    }

    private fun assertTypeParsed(type: String, expectedClass: KClass<*>, expectedArraySize: Int?) {
        val (clazz, arraySize) = parseType(type)
        assertEquals(expectedClass, clazz)
        assertEquals(expectedArraySize, arraySize)
    }
}