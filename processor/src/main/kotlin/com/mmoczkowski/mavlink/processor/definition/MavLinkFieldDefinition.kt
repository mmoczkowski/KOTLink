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

package com.mmoczkowski.mavlink.processor.definition

import com.mmoczkowski.mavlink.processor.ParsingException
import com.mmoczkowski.mavlink.processor.util.toCamelCase
import kotlin.reflect.KClass

data class MavLinkFieldDefinition(
    val type: String,
    val arraySize: Int?,
    val name: String,
    val enum: String?,
    val description: String,
    val isExtension: Boolean
) {
    @OptIn(ExperimentalUnsignedTypes::class)
    val clazz: KClass<*> = if (arraySize == null) {
        when (type) {
            "double" -> Double::class
            "char" -> Char::class
            "int8_t" -> Byte::class
            "int16_t" -> Short::class
            "int32_t" -> Int::class
            "int64_t" -> Long::class
            "uint8_t" -> UByte::class
            "uint16_t" -> UShort::class
            "uint32_t" -> UInt::class
            "uint64_t" -> ULong::class
            "float" -> Float::class
            else -> null
        }
    } else {
        when (type) {
            "double" -> DoubleArray::class
            "char" -> CharArray::class
            "int8_t" -> ByteArray::class
            "int16_t" -> ShortArray::class
            "int32_t" -> IntArray::class
            "int64_t" -> LongArray::class
            "uint8_t" -> UByteArray::class
            "uint16_t" -> UShortArray::class
            "uint32_t" -> UIntArray::class
            "uint64_t" -> ULongArray::class
            "float" -> FloatArray::class
            else -> null
        }
    } ?: throw ParsingException("Unsupported type $type")
    val propertyName: String = name.toCamelCase(false)
}
