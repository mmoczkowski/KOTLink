package com.mmoczkowski.mavlink

class MavUnsupportedMessage(private val content: ByteArray) : MavLinkMessage {
    override fun toBytes(): ByteArray = content
}
