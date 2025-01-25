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
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MavLinkParser(private vararg val protocols: MavLinkProtocol) {
    private companion object {
        const val MAX_PAYLOAD_SIZE = 255

        enum class ParserState {
            AWAIT_STX,
            AWAIT_PAYLOAD_LENGTH,
            AWAIT_INCOMPATIBILITY_FLAGS,
            AWAIT_COMPATIBILITY_FLAGS,
            AWAIT_SEQUENCE_NUMBER,
            AWAIT_SYSTEM_ID,
            AWAIT_COMPONENT_ID,
            AWAIT_MESSAGE_ID,
            AWAIT_PAYLOAD,
            AWAIT_CHECKSUM
        }

        fun ubytesToUshort(low: UByte, high: UByte): UShort =
            ((high.toUInt() shl 8) or low.toUInt()).toUShort()

        fun ubytesToUint(low: UByte, mid: UByte, high: UByte): UInt =
            ((high.toInt() shl 16) or (mid.toInt() shl 8) or low.toInt()).toUInt()
    }

    private var stx: UByte? = null
    private var payloadLength: UByte? = null
    private var inCompatibilityFlags: UByte? = null
    private var compatibilityFlags: UByte? = null
    private var sequenceNumber: UByte? = null
    private var systemId: UByte? = null
    private var componentId: UByte? = null
    private var messageIdLow: UByte? = null
    private var messageIdMid: UByte? = null
    private var messageIdHigh: UByte? = null
    private var payload: ByteBuffer = ByteBuffer.allocate(MAX_PAYLOAD_SIZE).order(ByteOrder.LITTLE_ENDIAN)
    private var checksumLow: UByte? = null
    private var checksumHigh: UByte? = null
    private var headerCrc: UShort = 0xffffu
    private var state: ParserState = ParserState.AWAIT_STX

    fun parseNextByte(byte: Byte): MavLinkFrame? {
        when (state) {
            ParserState.AWAIT_STX -> {
                val ubyte = byte.toUByte()
                if (ubyte == MavLinkFrame.V1.STX || ubyte == MavLinkFrame.V2.STX) {
                    stx = ubyte
                    state = ParserState.AWAIT_PAYLOAD_LENGTH
                }

                return null
            }

            ParserState.AWAIT_PAYLOAD_LENGTH -> {
                payloadLength = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }

                state = when (stx) {
                    MavLinkFrame.V1.STX -> ParserState.AWAIT_SEQUENCE_NUMBER
                    MavLinkFrame.V2.STX -> ParserState.AWAIT_INCOMPATIBILITY_FLAGS
                    else -> {
                        reset()
                        throw IllegalStateException("Invalid stx: $stx")
                    }
                }
                return null
            }

            // Skipped in v1
            ParserState.AWAIT_INCOMPATIBILITY_FLAGS -> {
                inCompatibilityFlags = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }
                state = ParserState.AWAIT_COMPATIBILITY_FLAGS
                return null
            }

            // Skipped in v1
            ParserState.AWAIT_COMPATIBILITY_FLAGS -> {
                compatibilityFlags = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }
                state = ParserState.AWAIT_SEQUENCE_NUMBER
                return null
            }

            ParserState.AWAIT_SEQUENCE_NUMBER -> {
                sequenceNumber = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }
                state = ParserState.AWAIT_SYSTEM_ID
                return null
            }

            ParserState.AWAIT_SYSTEM_ID -> {
                systemId = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }
                state = ParserState.AWAIT_COMPONENT_ID
                return null
            }

            ParserState.AWAIT_COMPONENT_ID -> {
                componentId = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }
                state = ParserState.AWAIT_MESSAGE_ID
                return null
            }

            ParserState.AWAIT_MESSAGE_ID -> {
                val ubyte = byte.toUByte().also {
                    headerCrc = headerCrc accumulate it
                }
                when {
                    messageIdLow == null -> {
                        messageIdLow = ubyte
                        if (stx == MavLinkFrame.V1.STX) {
                            state = if (payloadLength?.toInt() == 0) {
                                ParserState.AWAIT_CHECKSUM
                            } else {
                                ParserState.AWAIT_PAYLOAD
                            }
                        }
                    }

                    messageIdMid == null -> messageIdMid = ubyte
                    messageIdHigh == null -> {
                        messageIdHigh = ubyte
                        state = ParserState.AWAIT_PAYLOAD
                    }
                }
                return null
            }

            ParserState.AWAIT_PAYLOAD -> {
                payload.put(byte)
                if (payload.position() == payloadLength?.toInt()) {
                    state = ParserState.AWAIT_CHECKSUM
                }
                return null
            }

            ParserState.AWAIT_CHECKSUM -> {
                when {
                    checksumLow == null -> {
                        checksumLow = byte.toUByte()
                        return null
                    }

                    else -> {
                        checksumHigh = byte.toUByte()
                    }
                }
            }
        }

        return try {
            val messageId = when (stx) {
                MavLinkFrame.V1.STX -> messageIdLow?.toUInt()
                MavLinkFrame.V2.STX -> ubytesToUint(
                    low = messageIdLow ?: throw IllegalStateException(),
                    mid = messageIdMid ?: throw IllegalStateException(),
                    high = messageIdHigh ?: throw IllegalStateException(),
                )

                else -> null
            } ?: throw IllegalStateException("Invalid stx: $stx")

            val array = payload.array().copyOf()
            val message = protocols.firstNotNullOfOrNull { protocol ->
                protocol.fromBytes(
                    messageId,
                    array,
                    payload.position(),
                    headerCrc
                )
            } ?: MavLinkPayload(MavUnsupportedMessage(content = array), headerCrc)

            val checksum = MavLinkFrame.Checksum(
                expected = ubytesToUshort(
                    low = checksumLow ?: throw IllegalStateException(),
                    high = checksumHigh ?: throw IllegalStateException()
                ),
                actual = message.crc,
            )

             when (stx) {
                MavLinkFrame.V1.STX -> {
                    MavLinkFrame.V1(
                        sequenceNumber = sequenceNumber ?: throw IllegalStateException(),
                        systemId = systemId ?: throw IllegalStateException(),
                        componentId = componentId ?: throw IllegalStateException(),
                        messageId = messageId,
                        payload = message.message,
                        checksum = checksum,
                    )
                }

                MavLinkFrame.V2.STX -> {
                    MavLinkFrame.V2(
                        inCompatibilityFlags = inCompatibilityFlags ?: throw IllegalStateException(),
                        compatibilityFlags = compatibilityFlags ?: throw IllegalStateException(),
                        sequenceNumber = sequenceNumber ?: throw IllegalStateException(),
                        systemId = systemId ?: throw IllegalStateException(),
                        componentId = componentId ?: throw IllegalStateException(),
                        messageId = messageId,
                        payload = message.message,
                        checksum = checksum,
                    )
                }

                else -> throw IllegalStateException("Invalid stx: $stx")
            }
        } catch (exception: Throwable) {
            exception.printStackTrace()
            null
        } finally {
            reset()
        }
    }

    fun parseNextBytes(frame: ByteArray): List<MavLinkFrame> {
        val parsedFrames = mutableListOf<MavLinkFrame>()
        frame.forEach { byte ->
            val parsedFrame = parseNextByte(byte)
            if (parsedFrame != null) {
                parsedFrames += parsedFrame
            }
        }
        return parsedFrames.toList()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun parseNextBytes(frame: UByteArray): List<MavLinkFrame> = parseNextBytes(frame.toByteArray())

    private fun reset() {
        stx = null
        payloadLength = null
        inCompatibilityFlags = null
        compatibilityFlags = null
        sequenceNumber = null
        systemId = null
        componentId = null
        messageIdLow = null
        messageIdMid = null
        messageIdHigh = null
        payload.clear()
        payload.array().fill(0)
        checksumLow = null
        checksumHigh = null
        headerCrc = 0xffffu
        state = ParserState.AWAIT_STX
    }
}
