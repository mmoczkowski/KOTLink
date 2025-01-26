/*
 * Copyright (C) 2023 Miłosz Moczkowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mmoczkowski.mavlink.processor.definition

import com.mmoczkowski.mavlink.processor.ParsingException
import com.mmoczkowski.mavlink.processor.util.asIterable
import com.mmoczkowski.mavlink.processor.util.toCamelCase
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.reflect.KClass
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
                        val type: String = element.getAttribute("type")
                        val matchResult: MatchResult? = TYPE_PATTERN.toRegex().find(type)
                        val sanitisedType: String = matchResult?.groups?.get(1)?.value
                            ?: throw IllegalArgumentException("Unexpected type $type")
                        val arraySize: UByte? = try {
                            matchResult.groups[2]?.value?.toUByte()
                        } catch (e: IndexOutOfBoundsException) {
                            null
                        }

                        MavLinkFieldDefinition(
                            type = sanitisedType,
                            arraySize = arraySize,
                            name = element.getAttribute("name"),
                            enum = element.tryGetAttribute("enum")?.toCamelCase(true),
                            description = element.textContent.trim(),
                            isExtension = isExtension,
                            clazz = parseType(type = sanitisedType, isArray = arraySize != null)
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

    return MavLinkProtocolDefinition(name = "MavLink${nameWithoutExtension.toCamelCase(true)}Protocol", messages, enums)
}

private fun NodeList.asElementList(): List<Element> = (0 until length).map { index -> item(index) as Element }
private fun Element.tryGetAttribute(attribute: String): String? = if (hasAttribute(attribute)) {
    getAttribute(attribute)
} else {
    null
}

@OptIn(ExperimentalUnsignedTypes::class)
internal fun parseType(type: String, isArray: Boolean): KClass<*> = if (!isArray) {
    when (type) {
        "double" -> Double::class
        "char" -> Char::class
        "int8_t" -> Byte::class
        "int16_t" -> Short::class
        "int32_t" -> Int::class
        "int64_t" -> Long::class
        "uint8_t" -> UByte::class
        "uint16_t" -> UShort::class
        "uint32_t" -> UInt::class
        "uint64_t" -> ULong::class
        "float" -> Float::class
        else -> null
    }
} else {
    when (type) {
        "double" -> DoubleArray::class
        "char" -> CharArray::class
        "int8_t" -> ByteArray::class
        "int16_t" -> ShortArray::class
        "int32_t" -> IntArray::class
        "int64_t" -> LongArray::class
        "uint8_t" -> UByteArray::class
        "uint16_t" -> UShortArray::class
        "uint32_t" -> UIntArray::class
        "uint64_t" -> ULongArray::class
        "float" -> FloatArray::class
        else -> null
    }
} ?: throw ParsingException("Unsupported type $type")
