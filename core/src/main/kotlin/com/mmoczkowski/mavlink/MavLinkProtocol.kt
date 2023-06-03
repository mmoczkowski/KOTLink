package com.mmoczkowski.mavlink

interface MavLinkProtocol {
    fun fromBytes(messageId: UInt, payload: ByteArray): MavLinkMessage?
    fun getCrcExtra(messageId: UInt): Byte?
}
