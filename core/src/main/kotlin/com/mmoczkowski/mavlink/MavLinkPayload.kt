package com.mmoczkowski.mavlink

data class MavLinkPayload(
    val message: MavLinkMessage,
    val crc: UShort,
)
