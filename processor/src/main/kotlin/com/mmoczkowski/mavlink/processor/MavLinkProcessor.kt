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

package com.mmoczkowski.mavlink.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.mmoczkowski.mavlink.processor.definition.MavLinkProtocolDefinition
import com.mmoczkowski.mavlink.processor.definition.parseMavlinkDefinition
import java.io.File

internal class MavLinkProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    companion object {
        private const val ARG_SCHEMA_LOCATION = "mavlink.schemaLocation"
        private const val ARG_PACKAGE_NAME = "mavlink.packageName"
    }

    private var isFinishedProcessing: Boolean = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if(isFinishedProcessing) {
            return emptyList()
        }

        val schemaLocation = options[ARG_SCHEMA_LOCATION]
            ?: throw IllegalArgumentException("No schema location provided")

        logger.info("Mavlink schema location: $schemaLocation")

        val schemaDirectory = File(schemaLocation)

        val combinedProtocol = schemaDirectory.listFiles { _, name ->
            name.endsWith(".xml")
        }?.fold(listOf<MavLinkProtocolDefinition>()) { acc, file ->
            logger.info("Parsing: ${file.name}")
            acc.plus(file.parseMavlinkDefinition())
        }?.let { protocols ->
            MavLinkProtocolDefinition(
                name = protocols.joinToString { it.name },
                messages = protocols.flatMap { it.messages },
                enums = protocols.flatMap { it.enums },
            )
        } ?: throw IllegalArgumentException("No MAVLink schema found in $schemaDirectory")

        val packageName = options[ARG_PACKAGE_NAME]
            ?: throw IllegalArgumentException("No package name provided")

        combinedProtocol.generateConstants(codeGenerator, packageName)
        combinedProtocol.generateMessages(codeGenerator, packageName)
        combinedProtocol.generateProtocol(codeGenerator, packageName)

        isFinishedProcessing = true
        return emptyList()
    }
}

