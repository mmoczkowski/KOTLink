package com.mmoczkowski.mavlink.processor.definition

data class MavLinkEnumEntryDefinition(
    val name: String,
    val value: UInt,
    val description: String?
)
