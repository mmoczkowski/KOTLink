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
            ubyteArrayOf(253u, 9u, 0u, 0u, 127u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 162u, 43u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 135u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 248u, 35u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 139u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 94u, 91u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 147u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 18u, 170u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 151u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 112u, 130u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 159u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 180u, 210u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 163u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 155u, 64u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 171u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 95u, 16u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 175u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 61u, 56u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 183u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 113u, 201u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 187u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 215u, 177u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 195u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 152u, 157u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 199u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 250u, 181u),
            ubyteArrayOf(253u, 9u, 0u, 0u, 207u, 1u, 1u, 0u, 0u, 0u, 22u, 0u, 0u, 0u, 2u, 0u, 81u, 3u, 3u, 62u, 229u),
        )
        for (rawFrame in validV2HeartbeatFrames) {
            val parsedFrame = parser.parseNextBytes(rawFrame).first()
            assertFrameEquals(rawFrame, parsedFrame, MavHeartbeatMessage::class)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `when unsupported v2 frame expect parsed properly`() {
        val validV2UnsupportedFrames = listOf(
            ubyteArrayOf(253u, 31u, 0u, 0u, 32u, 1u, 1u, 1u, 0u, 0u, 11u, 0u, 1u, 1u, 11u, 0u, 1u, 1u, 11u, 0u, 0u, 1u, 70u, 0u, 0u, 0u, 255u, 255u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 100u, 148u, 146u),
            ubyteArrayOf(253u, 28u, 0u, 0u, 33u, 1u, 1u, 30u, 0u, 0u, 92u, 28u, 0u, 0u, 237u, 113u, 189u, 61u, 49u, 43u, 158u, 190u, 0u, 196u, 100u, 188u, 69u, 103u, 15u, 190u, 173u, 208u, 38u, 62u, 131u, 2u, 214u, 189u, 140u, 9u),
            ubyteArrayOf(253u, 20u, 0u, 0u, 34u, 1u, 1u, 74u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 103u, 1u, 245u, 255u, 95u, 199u),
            ubyteArrayOf(253u, 41u, 0u, 0u, 36u, 1u, 1u, 65u, 0u, 0u, 45u, 30u, 0u, 0u, 220u, 5u, 220u, 5u, 220u, 5u, 117u, 3u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 220u, 5u, 18u, 154u, 83u),
            ubyteArrayOf(253u, 36u, 0u, 0u, 37u, 1u, 1u, 147u, 0u, 0u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u, 0u, 0u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 0u, 0u, 0u, 255u, 49u, 93u),
            ubyteArrayOf(253u, 14u, 0u, 0u, 38u, 1u, 1u, 29u, 0u, 0u, 45u, 30u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 36u, 9u, 218u, 221u),
            ubyteArrayOf(253u, 51u, 0u, 0u, 39u, 1u, 1u, 253u, 0u, 0u, 5u, 78u, 79u, 32u, 82u, 67u, 32u, 76u, 73u, 78u, 75u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 32u, 182u, 8u),
            ubyteArrayOf(253u, 31u, 0u, 0u, 40u, 1u, 1u, 1u, 0u, 0u, 11u, 0u, 1u, 1u, 11u, 0u, 1u, 1u, 11u, 0u, 0u, 1u, 70u, 0u, 0u, 0u, 255u, 255u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 100u, 129u, 236u),
            ubyteArrayOf(253u, 28u, 0u, 0u, 41u, 1u, 1u, 30u, 0u, 0u, 106u, 30u, 0u, 0u, 237u, 113u, 189u, 61u, 184u, 244u, 159u, 190u, 0u, 196u, 100u, 188u, 212u, 240u, 21u, 62u, 95u, 131u, 134u, 190u, 206u, 66u, 154u, 61u, 185u, 75u),
            ubyteArrayOf(253u, 20u, 0u, 0u, 42u, 1u, 1u, 74u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 103u, 1u, 245u, 255u, 61u, 69u),
            ubyteArrayOf(253u, 31u, 0u, 0u, 44u, 1u, 1u, 1u, 0u, 0u, 11u, 0u, 1u, 1u, 11u, 0u, 1u, 1u, 11u, 0u, 0u, 1u, 70u, 0u, 0u, 0u, 255u, 255u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 100u, 131u, 87u),
            ubyteArrayOf(253u, 28u, 0u, 0u, 45u, 1u, 1u, 30u, 0u, 0u, 120u, 32u, 0u, 0u, 237u, 113u, 189u, 61u, 184u, 244u, 159u, 190u, 0u, 196u, 100u, 188u, 205u, 75u, 205u, 60u, 85u, 186u, 31u, 61u, 239u, 90u, 160u, 61u, 165u, 210u),
            ubyteArrayOf(253u, 20u, 0u, 0u, 46u, 1u, 1u, 74u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 103u, 1u, 245u, 255u, 12u, 4u),
        )

        for (rawFrame in validV2UnsupportedFrames) {
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
        val validFrame1 = ubyteArrayOf(0xfeu, 0x9u, 0x0u, 0x4fu, 0x4cu, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x13u, 0xau, 0x0u, 0x2u, 0x0u, 0xe6u, 0x71u)
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
