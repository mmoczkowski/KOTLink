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
