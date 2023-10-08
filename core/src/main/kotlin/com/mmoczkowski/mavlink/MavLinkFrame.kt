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

sealed class MavLinkFrame {

    abstract val sequenceNumber: UByte
    abstract val systemId: UByte
    abstract val componentId: UByte
    abstract val payload: MavLinkMessage

    abstract fun toBytes(): ByteArray

    data class V1(
        override val sequenceNumber: UByte,
        override val systemId: UByte,
        override val componentId: UByte,
        override val payload: MavLinkMessage,
    ) : MavLinkFrame() {
        companion object {
            const val STX: UByte = 0xFEu
        }

        override fun toBytes(): ByteArray {
            TODO("Not yet implemented")
        }
    }

    data class V2(
        val inCompatibilityFlags: UByte,
        val compatibilityFlags: UByte,
        override val sequenceNumber: UByte,
        override val systemId: UByte,
        override val componentId: UByte,
        override val payload: MavLinkMessage,
    ) : MavLinkFrame() {
        companion object {
            const val STX: UByte = 0xFDu
        }

        override fun toBytes(): ByteArray {
            TODO("Not yet implemented")
        }
    }
}
