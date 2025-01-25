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

import com.google.devtools.ksp.processing.CodeGenerator
import com.mmoczkowski.mavlink.MavLinkPayload
import com.mmoczkowski.mavlink.MavLinkProtocol
import com.mmoczkowski.mavlink.processor.definition.MavLinkProtocolDefinition
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.writeTo

internal fun MavLinkProtocolDefinition.generateProtocol(codeGenerator: CodeGenerator, packageName: String) {

    val fromBytesCodeBlock = messages.joinToString(
        separator = "\n",
        prefix = "return when(messageId) {\n",
        postfix = "\nelse -> null\n}"
    ) { message ->
        "\t${message.id}u -> ${message.className}.fromBytes(payload, payloadLength, headerCrc)"
    }

    FileSpec
        .builder(packageName = packageName, fileName = name)
        .addType(
            TypeSpec
                .objectBuilder(ClassName(packageName, name))
                .addSuperinterface(MavLinkProtocol::class.asTypeName())
                .addFunction(
                    FunSpec
                        .builder("fromBytes")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("messageId", UInt::class)
                        .addParameter("payload", ByteArray::class)
                        .addParameter("payloadLength", Int::class)
                        .addParameter("headerCrc", UShort::class)
                        .returns(MavLinkPayload::class.asTypeName().copy(nullable = true))
                        .addCode(fromBytesCodeBlock)
                        .build()
                )
                .build()
        )

        .build().writeTo(codeGenerator, false)
}
