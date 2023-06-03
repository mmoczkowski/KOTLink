package com.mmoczkowski.mavlink

interface MavLinkMessage {
    fun toBytes(): ByteArray
}
