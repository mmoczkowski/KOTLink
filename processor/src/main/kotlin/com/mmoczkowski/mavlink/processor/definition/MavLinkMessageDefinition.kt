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

import com.mmoczkowski.mavlink.processor.util.toCamelCase
import com.mmoczkowski.mavlink.util.accumulate
import kotlin.reflect.KClass

data class MavLinkMessageDefinition(
    val id: UInt,
    val name: String,
    val description: String,
    val fields: List<MavLinkFieldDefinition>
) {
    val className: String = "MavLink${name.toCamelCase(true)}Message"

    val orderedFields: List<MavLinkFieldDefinition> = fields
        .sortedByDescending { field ->
            if (field.isExtension) {
                0u
            } else {
                field.clazz.typeSize()
            }
        }

    val crcExtra: Byte = 0xFFFFu.toUShort().run {
        var crc = this accumulate "$name "
        fields
        orderedFields
            .filterNot { field -> field.isExtension }
            .forEach { field ->
                crc = crc accumulate "${field.type} "
                crc = crc accumulate "${field.name} "
                if (field.arraySize != null) {
                    crc = crc accumulate field.arraySize.toUByte()
                }
            }

        (crc.toInt() and 0x00FF xor (crc.toInt() shr 8 and 0x00FF)).toByte()
    }

    val lengthWithoutExtensions: UByte = fields
        .filterNot(MavLinkFieldDefinition::isExtension)
        .fold(initial = 0u) { acc, field ->
            val arraySize = field.arraySize
            val typeSize = field.clazz.typeSize()
            if (arraySize == null) {
                (acc + typeSize).toUByte()
            } else {
                (acc + arraySize * typeSize).toUByte()
            }
        }

    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        fun KClass<*>.typeSize(): UByte = when (this) {
            ULong::class, ULongArray::class, Long::class, LongArray::class, Double::class, DoubleArray::class -> 8u
            UInt::class, UIntArray::class, Int::class, IntArray::class, Float::class, FloatArray::class -> 4u
            UShort::class, UShortArray::class, Short::class, ShortArray::class -> 2u
            UByte::class, UByteArray::class, Byte::class, ByteArray::class, Char::class, CharArray::class -> 1u
            else -> throw IllegalArgumentException("Unsupported type ${this.simpleName}")
        }
    }
}
