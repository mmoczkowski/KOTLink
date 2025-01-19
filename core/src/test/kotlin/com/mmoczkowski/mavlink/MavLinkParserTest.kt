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

import com.mmoczkowski.mavlink.minimal.MavHeartbeatMessage
import com.mmoczkowski.mavlink.minimal.MavMinimalProtocol
import org.junit.Test
import kotlin.test.assertFalse

class MavLinkParserTest {

    private val parser = MavLinkParser(MavMinimalProtocol)

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when valid v1 frame expect parsed properly`() {
        val validV1HeartbeatFrames = listOf(
            ubyteArrayOf(0xfeu, 0x9u, 0x0u, 0x4fu, 0x4cu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x13u, 0xau, 0x0u, 0x2u, 0x0u, 0xe6u, 0x71u),
            ubyteArrayOf(0xfeu, 0x9u, 0x1u, 0xf5u, 0xd9u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x3u, 0x8u, 0x0u, 0x1u, 0x2u, 0x3bu, 0xb3u),
            ubyteArrayOf(0xfeu, 0x9u, 0x2u, 0x2bu, 0xf9u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x14u, 0xfu, 0x0u, 0x4u, 0x1u, 0x78u, 0xa9u),
            ubyteArrayOf(0xfeu, 0x9u, 0x3u, 0x33u, 0x1cu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x2au, 0x9u, 0x0u, 0x5u, 0x2u, 0x47u, 0x47u),
            ubyteArrayOf(0xfeu, 0x9u, 0x4u, 0xa8u, 0x69u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x14u, 0x14u, 0x0u, 0x0u, 0x1u, 0x95u, 0x44u),
            ubyteArrayOf(0xfeu, 0x9u, 0x5u, 0x50u, 0xedu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0xeu, 0x8u, 0x0u, 0x5u, 0x1u, 0x51u, 0x67u),
        )

        for (rawFrame in validV1HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFrameEquals(rawFrame, parsedFrame, MavHeartbeatMessage::class)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when unsupported v1 frame expect parsed properly`() {
        val validV1HeartbeatFrames = listOf(
            ubyteArrayOf(0xfeu, 0x9u, 0x0u, 0x4fu, 0x4cu, 0x1u, 0x0u, 0x0u, 0x0u, 0x0u, 0x13u, 0xau, 0x0u, 0x2u, 0x0u, 0xe6u, 0x71u),
            ubyteArrayOf(0xfeu, 0x9u, 0x1u, 0xf5u, 0xd9u, 0x2u, 0x0u, 0x0u, 0x0u, 0x0u, 0x3u, 0x8u, 0x0u, 0x1u, 0x2u, 0x3bu, 0xb3u),
            ubyteArrayOf(0xfeu, 0x9u, 0x2u, 0x2bu, 0xf9u, 0x3u, 0x0u, 0x0u, 0x0u, 0x0u, 0x14u, 0xfu, 0x0u, 0x4u, 0x1u, 0x78u, 0xa9u),
            ubyteArrayOf(0xfeu, 0x9u, 0x3u, 0x33u, 0x1cu, 0x4u, 0x0u, 0x0u, 0x0u, 0x0u, 0x2au, 0x9u, 0x0u, 0x5u, 0x2u, 0x47u, 0x47u),
            ubyteArrayOf(0xfeu, 0x9u, 0x4u, 0xa8u, 0x69u, 0x5u, 0x0u, 0x0u, 0x0u, 0x0u, 0x14u, 0x14u, 0x0u, 0x0u, 0x1u, 0x95u, 0x44u),
            ubyteArrayOf(0xfeu, 0x9u, 0x5u, 0x50u, 0xedu, 0x6u, 0x0u, 0x0u, 0x0u, 0x0u, 0xeu, 0x8u, 0x0u, 0x5u, 0x1u, 0x51u, 0x67u),
        )

        for (rawFrame in validV1HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFrameEquals(rawFrame, parsedFrame, MavUnsupportedMessage::class)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when valid v2 frame expect parsed properly`() {
        val validV2HeartbeatFrames = listOf(
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x0u, 0x31u, 0x48u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x28u, 0x10u, 0x0u, 0x6u, 0x0u, 0xa3u, 0x70u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x1u, 0x1eu, 0x56u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x16u, 0xfu, 0x0u, 0x1u, 0x2u, 0x7du, 0xbau),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x2u, 0x51u, 0x24u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x24u, 0x3u, 0x0u, 0x3u, 0x1u, 0xeeu, 0xf0u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x3u, 0xe2u, 0x20u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x27u, 0x5u, 0x0u, 0x2u, 0x0u, 0x90u, 0xf1u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x4u, 0xdu, 0x83u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x19u, 0x2u, 0x0u, 0x5u, 0x2u, 0x3cu, 0xdau),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x5u, 0x6du, 0x68u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x20u, 0xeu, 0x0u, 0x5u, 0x2u, 0x87u, 0x1bu),
        )

        for (rawFrame in validV2HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFrameEquals(rawFrame, parsedFrame, MavHeartbeatMessage::class)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when unsupported v2 frame expect parsed properly`() {
        val validV2HeartbeatFrames = listOf(
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x0u, 0x31u, 0x48u, 0x1u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x28u, 0x10u, 0x0u, 0x6u, 0x0u, 0xa3u, 0x70u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x1u, 0x1eu, 0x56u, 0x0u, 0x2u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x16u, 0xfu, 0x0u, 0x1u, 0x2u, 0x7du, 0xbau),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x2u, 0x51u, 0x24u, 0x0u, 0x0u, 0x5u, 0x0u, 0x0u, 0x0u, 0x0u, 0x24u, 0x3u, 0x0u, 0x3u, 0x1u, 0xeeu, 0xf0u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x3u, 0xe2u, 0x20u, 0x0u, 0x4u, 0x1u, 0x0u, 0x0u, 0x0u, 0x0u, 0x27u, 0x5u, 0x0u, 0x2u, 0x0u, 0x90u, 0xf1u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x4u, 0xdu, 0x83u, 0x0u, 0x6u, 0x2u, 0x0u, 0x0u, 0x0u, 0x0u, 0x19u, 0x2u, 0x0u, 0x5u, 0x2u, 0x3cu, 0xdau),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x5u, 0x6du, 0x68u, 0x1u, 0x1u, 0x2u, 0x0u, 0x0u, 0x0u, 0x0u, 0x20u, 0xeu, 0x0u, 0x5u, 0x2u, 0x87u, 0x1bu),
        )

        for (rawFrame in validV2HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFrameEquals(rawFrame, parsedFrame, MavUnsupportedMessage::class)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when v1 frame with invalid checksum expect exception`() {
        val invalidV1HeartbeatFrames = listOf(
            ubyteArrayOf(0xfeu, 0x9u, 0x0u, 0x4fu, 0x4cu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x13u, 0xau, 0x0u, 0x2u, 0x0u, 0xe6u, 0x70u),
            ubyteArrayOf(0xfeu, 0x9u, 0x1u, 0xf5u, 0xd9u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x3u, 0x8u, 0x0u, 0x1u, 0x2u, 0x4bu, 0xb3u),
            ubyteArrayOf(0xfeu, 0x9u, 0x2u, 0x2bu, 0xf9u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x14u, 0xfu, 0x0u, 0x4u, 0x1u, 0x76u, 0xa9u),
            ubyteArrayOf(0xfeu, 0x9u, 0x3u, 0x33u, 0x1cu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x2au, 0x9u, 0x0u, 0x5u, 0x2u, 0x47u, 0x27u),
            ubyteArrayOf(0xfeu, 0x9u, 0x4u, 0xa8u, 0x69u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x14u, 0x14u, 0x0u, 0x0u, 0x1u, 0x95u, 0x43u),
            ubyteArrayOf(0xfeu, 0x9u, 0x5u, 0x50u, 0xedu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0xeu, 0x8u, 0x0u, 0x5u, 0x1u, 0x51u, 0x7u),
        )

        for (rawFrame in invalidV1HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFalse(parsedFrame.checksum.isValid)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when v2 frame with invalid checksum expect exception`() {
        val invalidV2HeartbeatFrames = listOf(
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x0u, 0x31u, 0x48u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x28u, 0x10u, 0x0u, 0x6u, 0x0u, 0xa3u, 0x71u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x1u, 0x1eu, 0x56u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x16u, 0xfu, 0x0u, 0x1u, 0x2u, 0x7du, 0x9au),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x2u, 0x51u, 0x24u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x24u, 0x3u, 0x0u, 0x3u, 0x1u, 0xeeu, 0xf1u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x3u, 0xe2u, 0x20u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x27u, 0x5u, 0x0u, 0x2u, 0x0u, 0x90u, 0xe1u),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x4u, 0xdu, 0x83u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x19u, 0x2u, 0x0u, 0x5u, 0x2u, 0x3du, 0xdau),
            ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x5u, 0x6du, 0x68u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x20u, 0xeu, 0x0u, 0x5u, 0x2u, 0x97u, 0x1bu),
        )

        for (rawFrame in invalidV2HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFalse(parsedFrame.checksum.isValid)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when invalid frame after valid frame expect parsed properly`() {
        val validFrame1 = ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x0u, 0x31u, 0x48u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x28u, 0x10u, 0x0u, 0x6u, 0x0u, 0xa3u, 0x70u)
        val parsedValidFrame1 = parser.parseNextBytes(validFrame1)
        assertFrameEquals(validFrame1, parsedValidFrame1.first(), MavHeartbeatMessage::class)

        val invalidFrame = ubyteArrayOf(0xfdu, 0x9u, 0x0u, 0x0u, 0x1u, 0x1eu, 0x56u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x16u, 0xfu, 0x0u, 0x1u, 0x2u, 0x7du, 0x9au)
        val parsedInvalidFrame = parser.parseNextBytes(invalidFrame).first()
        assertFalse(parsedInvalidFrame.checksum.isValid)

        val validFrame2 = ubyteArrayOf(0xfeu, 0x9u, 0x3u, 0x33u, 0x1cu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x2au, 0x9u, 0x0u, 0x5u, 0x2u, 0x47u, 0x47u)
        val parsedValidFrame2 = parser.parseNextBytes(validFrame2)
        assertFrameEquals(validFrame2, parsedValidFrame2.first(), MavHeartbeatMessage::class)
    }
}
