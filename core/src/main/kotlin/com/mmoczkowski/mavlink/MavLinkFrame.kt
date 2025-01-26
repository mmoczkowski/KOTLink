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

package com.mmoczkowski.mavlink

import com.mmoczkowski.mavlink.util.accumulate
import com.mmoczkowski.mavlink.util.putNext
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN

sealed interface MavLinkFrame {

    val sequenceNumber: UByte
    val systemId: UByte
    val componentId: UByte
    val messageId: UInt
    val payload: MavLinkMessage

    fun toBytes(): ByteArray

    data class V1(
        override val sequenceNumber: UByte,
        override val systemId: UByte,
        override val componentId: UByte,
        override val messageId: UInt,
        override val payload: MavLinkMessage,
    ) : MavLinkFrame {
        companion object {
            const val STX: UByte = 0xFEu
            const val MAX_FRAME_SIZE: Int = 263
        }

        override fun toBytes(): ByteArray {
            val buffer = ByteBuffer.allocate(MAX_FRAME_SIZE).order(LITTLE_ENDIAN).apply {
                putNext(STX)
                val payloadBytes = payload.toBytes()
                val length = payloadBytes.size.toUByte()
                putNext(length)
                putNext(sequenceNumber)
                putNext(systemId)
                putNext(componentId)
                putNext(messageId.toUByte())
                putNext(payloadBytes)

                val checksum = array()
                    .take(position())
                    .drop(1)
                    .plus(payload.crcExtra)
                    .fold((0xFFFFu).toUShort()) { crc, byte ->
                        crc accumulate byte.toUByte()
                    }

                putNext(checksum)
            }
            val frameSize: Int = buffer.position()
            return buffer.array().copyOf(newSize = frameSize)
        }
    }

    data class V2(
        val inCompatibilityFlags: UByte,
        val compatibilityFlags: UByte,
        override val sequenceNumber: UByte,
        override val systemId: UByte,
        override val componentId: UByte,
        override val messageId: UInt,
        override val payload: MavLinkMessage,
    ) : MavLinkFrame {
        companion object {
            const val STX: UByte = 0xFDu
            const val MAX_FRAME_SIZE: Int = 280
        }

        override fun toBytes(): ByteArray {
            val buffer = ByteBuffer.allocate(MAX_FRAME_SIZE).order(LITTLE_ENDIAN).apply {
                putNext(STX)
                val payloadBytes = payload
                    .toBytes()
                    .dropLastWhile { byte ->
                        byte == (0).toByte()
                    }
                    .toByteArray()
                val length = payloadBytes.size.toUByte()
                putNext(length)
                putNext(inCompatibilityFlags)
                putNext(compatibilityFlags)
                putNext(sequenceNumber)
                putNext(systemId)
                putNext(componentId)
                putNext(messageId.toUByte())
                putNext((messageId shr 8).toUByte())
                putNext((messageId shr 16).toUByte())
                putNext(payloadBytes)

                val checksum = array()
                    .take(position())
                    .drop(1)
                    .plus(payload.crcExtra)
                    .fold((0xFFFFu).toUShort()) { crc, byte ->
                        crc accumulate byte.toUByte()
                    }

                putNext(checksum)
            }
            val frameSize: Int = buffer.position()
            return buffer.array().copyOf(newSize = frameSize)
        }
    }
}
