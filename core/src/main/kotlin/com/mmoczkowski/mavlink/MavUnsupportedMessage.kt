package com.mmoczkowski.mavlink

data class MavUnsupportedMessage(private val content: ByteArray) : MavLinkMessage {
    override fun toBytes(): ByteArray = content
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MavUnsupportedMessage

        return content.contentEquals(other.content)
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
