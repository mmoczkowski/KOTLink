package com.mmoczkowski.mavlink.processor.definition

data class MavLinkEnumDefinition(
    val name: String,
    val description: String?,
    val entries: List<MavLinkEnumEntryDefinition>
)
