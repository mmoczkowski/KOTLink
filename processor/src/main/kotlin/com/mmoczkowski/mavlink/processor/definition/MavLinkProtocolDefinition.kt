package com.mmoczkowski.mavlink.processor.definition

data class MavLinkProtocolDefinition(
    val name: String,
    val messages: List<MavLinkMessageDefinition>,
    val enums: List<MavLinkEnumDefinition>
)
