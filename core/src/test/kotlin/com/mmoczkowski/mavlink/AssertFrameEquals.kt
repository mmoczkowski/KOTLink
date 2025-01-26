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

import com.mmoczkowski.mavlink.util.getNext
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

@OptIn(ExperimentalUnsignedTypes::class)
internal fun assertFrameEquals(rawFrame: UByteArray, parsedFrame: MavLinkFrame, expectedClass: KClass<*>) {
    val buffer = ByteBuffer.wrap(rawFrame.toByteArray())
    val stx: UByte = buffer.getNext()
    val payloadLength: UByte = buffer.getNext()

    if (stx == MavLinkFrame.V2.STX) {
        assertIs<MavLinkFrame.V2>(parsedFrame)
        val inCompatibilityFlags = buffer.get().toUByte()
        assertEquals(inCompatibilityFlags, parsedFrame.inCompatibilityFlags)
        val compatibilityFlags = buffer.get().toUByte()
        assertEquals(compatibilityFlags, parsedFrame.compatibilityFlags)
    } else if (stx == MavLinkFrame.V1.STX) {
        assertIs<MavLinkFrame.V1>(parsedFrame)
    } else {
        fail("Unsupported STX: $stx")
    }

    val sequenceNumber = buffer.get().toUByte()
    assertEquals(
        sequenceNumber,
        parsedFrame.sequenceNumber,
        "Sequence numbers do not match: raw = $sequenceNumber, parsed = ${parsedFrame.sequenceNumber}"
    )
    val systemId = buffer.get().toUByte()
    assertEquals(
        systemId,
        parsedFrame.systemId,
        "System IDs do not match: raw = $systemId, parsed = ${parsedFrame.systemId}"
    )

    val componentId = buffer.get().toUByte()
    assertEquals(
        componentId,
        parsedFrame.componentId,
        "Component IDs do not match: raw = $componentId, parsed = ${parsedFrame.componentId}"
    )

    val messageId: UInt = if (stx == MavLinkFrame.V1.STX) {
        buffer.getNext<UByte>().toUInt()
    } else if (stx == MavLinkFrame.V2.STX) {
        val messageIdLow: UByte = buffer.getNext()
        val messageIdMid: UByte = buffer.getNext()
        val messageIdHigh: UByte = buffer.getNext()
        ((messageIdHigh.toInt() shl 16) or (messageIdMid.toInt() shl 8) or messageIdLow.toInt()).toUInt()
    } else {
        fail("Unsupported STX: $stx")
    }

    assertEquals(
        messageId,
        parsedFrame.messageId,
    )

    val payload: ByteArray = buffer.getNext(payloadLength.toInt())
    assertContentEquals(
        payload,
        parsedFrame.payload.toBytes(),
    )

    assertEquals(expectedClass, parsedFrame.payload::class)
    assertContentEquals(rawFrame, parsedFrame.toBytes().toUByteArray())
}
