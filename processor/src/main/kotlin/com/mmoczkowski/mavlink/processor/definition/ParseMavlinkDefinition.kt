package com.mmoczkowski.mavlink.processor.definition

import com.mmoczkowski.mavlink.processor.util.asIterable
import com.mmoczkowski.mavlink.processor.util.toCamelCase
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.NodeList

private const val TYPE_PATTERN =
    """(uint[0-9]+_t|int[0-9]*_t|double|char|float)(?=(?:_mavlink_version)?(?:\[([0-9]+)])?${'$'})"""

fun File.parseMavlinkDefinition(): MavLinkProtocolDefinition {
    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docBuilder.parse(this)

    val mavlinkElement = doc.documentElement

    val messages = mavlinkElement.getElementsByTagName("message").asIterable().map { messageNode ->
        val messageElement = messageNode as Element
        val id = messageElement.getAttribute("id").toUInt()
        val name = messageElement.getAttribute("name")
        val description = messageElement.getElementsByTagName("description").item(0).textContent.trim()

        var isExtension = false
        val fields = mutableListOf<MavLinkFieldDefinition>()
        messageNode.childNodes.asIterable()
            .mapNotNull { node -> node as? Element }
            .forEach { element ->
                when (element.nodeName) {
                    "extensions" -> isExtension = true
                    "field" -> {
                        val type = element.getAttribute("type")
                        val matchResult = TYPE_PATTERN.toRegex().find(type)
                        val sanitisedType = matchResult?.groups?.get(1)?.value
                            ?: throw IllegalArgumentException("Unexpected type $type")
                        val arraySize = try {
                            matchResult.groups[2]?.value?.toInt()
                        } catch (e: IndexOutOfBoundsException) {
                            null
                        }

                        MavLinkFieldDefinition(
                            type = sanitisedType,
                            arraySize = arraySize,
                            name = element.getAttribute("name"),
                            enum = element.tryGetAttribute("enum")?.toCamelCase(true),
                            description = element.textContent.trim(),
                            isExtension = isExtension
                        ).let(fields::add)
                    }
                }
            }

        MavLinkMessageDefinition(id, name, description, fields)
    }

    val enums = mavlinkElement.getElementsByTagName("enum").asElementList().map { enumElement ->
        val name = "${enumElement.getAttribute("name").toCamelCase(true)}Const"
        val description =
            enumElement.getElementsByTagName("description").asElementList().firstOrNull()?.textContent?.trim()
        val entries = enumElement.getElementsByTagName("entry").asElementList().map { entryElement ->
            val name = entryElement.getAttribute("name")
            val value = entryElement.getAttribute("value").toUInt()
            val description =
                entryElement.getElementsByTagName("description").asElementList().firstOrNull()?.textContent
            MavLinkEnumEntryDefinition(name, value, description)
        }
        MavLinkEnumDefinition(name, description, entries)
    }

    return MavLinkProtocolDefinition(name = "Mav${nameWithoutExtension.toCamelCase(true)}Protocol", messages, enums)
}

private fun NodeList.asElementList(): List<Element> = (0 until length).map { index -> item(index) as Element }
private fun Element.tryGetAttribute(attribute: String): String? = if (hasAttribute(attribute)) {
    getAttribute(attribute)
} else {
    null
}
