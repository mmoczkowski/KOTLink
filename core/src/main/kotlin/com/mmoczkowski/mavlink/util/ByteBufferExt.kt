/*
 * Copyright (C) 2023 Miłosz Moczkowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mmoczkowski.mavlink.util

import java.nio.ByteBuffer

inline fun <reified T> ByteBuffer.getNext(): T =
    when (T::class) {
        Byte::class -> get()
        UByte::class -> get().toUByte()
        Char::class -> char
        Short::class -> short
        UShort::class -> short.toUShort()
        Int::class -> int
        UInt::class -> int.toUInt()
        Long::class -> long
        ULong::class -> long.toULong()
        Float::class -> float
        Double::class -> getDouble()
        else -> throw IllegalArgumentException("Unsupported type ${T::class.simpleName}")
    } as T

@OptIn(ExperimentalUnsignedTypes::class)
inline fun <reified T> ByteBuffer.getNext(size: Int): T =
    when (T::class) {
        ByteArray::class -> ByteArray(size) { get() } as T
        UByteArray::class -> UByteArray(size) { get().toUByte() } as T
        CharArray::class -> CharArray(size) { char } as T
        ShortArray::class -> ShortArray(size) { short } as T
        UShortArray::class -> UShortArray(size) { short.toUShort() } as T
        IntArray::class -> IntArray(size) { int } as T
        UIntArray::class -> UIntArray(size) { int.toUInt() } as T
        FloatArray::class -> FloatArray(size) { float } as T
        DoubleArray::class -> DoubleArray(size) { double } as T
        else -> throw IllegalArgumentException("Unsupported type ${T::class.simpleName}")
    }

@OptIn(ExperimentalUnsignedTypes::class)
inline fun <reified T> ByteBuffer.putNext(value: T): ByteBuffer {
    when (value) {
        is Byte -> put(value)
        is ByteArray -> put(value)
        is UByte -> put(value.toByte())
        is UByteArray -> put(value.toByteArray())
        is Char -> putChar(value)
        is CharArray -> value.forEach(::putChar)
        is Short -> putShort(value)
        is ShortArray -> value.forEach(::putShort)
        is UShort -> putShort(value.toShort())
        is UShortArray -> value.toShortArray().forEach(::putShort)
        is Int -> putInt(value)
        is IntArray -> value.forEach(::putInt)
        is UInt -> putInt(value.toInt())
        is UIntArray -> value.toIntArray().forEach(::putInt)
        is Float -> putFloat(value)
        is FloatArray -> value.forEach(::putFloat)
        is Double -> putDouble(value)
        is DoubleArray -> value.forEach(::putDouble)
        else -> throw IllegalArgumentException("Unsupported type ${T::class.simpleName}")
    }
    return this
}
