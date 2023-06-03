package com.mmoczkowski.mavlink

import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalUnsignedTypes::class)
internal fun assertFrameEquals(rawFrame: UByteArray, parsedFrame: MavLinkFrame, expectedClass: KClass<*>) {
    val buffer = ByteBuffer.wrap(rawFrame.toByteArray())
    val stx = buffer.get().toUByte()
    val payloadLength = buffer.get()

    if (stx == MavLinkFrame.V2.STX) {
        assertIs<MavLinkFrame.V2>(parsedFrame)
        val inCompatibilityFlags = buffer.get().toUByte()
        assertEquals(inCompatibilityFlags, parsedFrame.inCompatibilityFlags)
        val compatibilityFlags = buffer.get().toUByte()
        assertEquals(compatibilityFlags, parsedFrame.compatibilityFlags)
    } else if (stx == MavLinkFrame.V1.STX) {
        assertIs<MavLinkFrame.V1>(parsedFrame)
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

    assertEquals(expectedClass, parsedFrame.payload::class)

    val payload = ByteArray(payloadLength.toInt())
    buffer.get(payload)
}
