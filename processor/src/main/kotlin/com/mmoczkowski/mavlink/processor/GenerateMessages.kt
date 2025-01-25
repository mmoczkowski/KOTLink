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
import com.mmoczkowski.mavlink.MavLinkMessage
import com.mmoczkowski.mavlink.MavLinkPayload
import com.mmoczkowski.mavlink.processor.definition.MavLinkProtocolDefinition
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal fun MavLinkProtocolDefinition.generateMessages(codeGenerator: CodeGenerator, packageName: String) {
    messages.forEach { message ->
        val className = ClassName(packageName, message.className)

        val classBuilder = TypeSpec.classBuilder(className)
            .addModifiers(KModifier.DATA)
            .addSuperinterface(MavLinkMessage::class.asTypeName())
        val constructorBuilder = FunSpec.constructorBuilder()
        val fileSpecBuilder = FileSpec.builder(className)
        classBuilder
            .primaryConstructor(
                constructorBuilder
                    .apply {
                        message.fields.forEach { field ->
                            val fieldClassName = field.clazz.asClassName()
                            val fieldTypeName = fieldClassName.copy(nullable = field.isExtension)

                            val param = ParameterSpec.builder(field.propertyName, fieldTypeName)
                                .apply {
                                    if (field.isExtension) {
                                        defaultValue("%S", null)
                                    }
                                }
                                .build()

                            constructorBuilder.addParameter(param).build()

                            classBuilder.addProperty(
                                PropertySpec
                                    .builder(field.propertyName, fieldTypeName)
                                    .initializer(field.propertyName)
                                    .apply {
                                        if (field.isExtension) {
                                            addKdoc("Extension field. ${field.description}")
                                        } else {
                                            addKdoc(field.description.replace("%", "%%"))
                                        }
                                    }
                                    .build()
                            )
                        }
                    }
                    .build()
            ).addType(
                TypeSpec
                    .companionObjectBuilder()
                    .addFunction(
                        FunSpec
                            .builder("fromBytes")
                            .addParameter("data", ByteArray::class)
                            .addParameter("payloadLength", Int::class)
                            .addParameter("headerCrc", UShort::class)
                            .returns(MavLinkPayload::class.asTypeName())
                            .addStatement("val payloadLengthWithoutExtensionFields = min(payloadLength, ${message.maxPayloadLengthWithoutExtensionFields})")
                            .addStatement("val crc = data.take(payloadLengthWithoutExtensionFields).plus(CRC_EXTRA).fold(headerCrc) { crc, byte -> crc accumulate byte.toUByte() }")
                            .addStatement(
                                "val buffer = %T.wrap(data).order(%M)\nval message  = ${message.className}(",
                                ByteBuffer::class, MemberName(
                                    ByteOrder::class.asClassName(),
                                    "LITTLE_ENDIAN"
                                )
                            )
                            .apply {
                                message.orderedFields.forEach { field ->
                                    val sizeArgument = if (field.arraySize != null) "${field.arraySize}" else ""
                                    val getNextMemberName = MemberName(
                                        packageName = "com.mmoczkowski.mavlink.util",
                                        simpleName = "getNext"
                                    )
                                    if (field.isExtension) {
                                        addStatement(
                                            format = "\t${field.propertyName} = if(buffer.hasRemaining()) buffer.%M($sizeArgument) else null,",
                                            getNextMemberName
                                        )
                                    } else {
                                        addStatement(
                                            format = "\t${field.propertyName} = buffer.%M($sizeArgument),",
                                            getNextMemberName
                                        )
                                    }
                                }
                            }
                            .addStatement(")")
                            .addStatement("return MavLinkPayload(message, crc)")
                            .build()
                    )
                    .addProperty(
                        PropertySpec
                            .builder("MESSAGE_ID", UInt::class)
                            .addModifiers(KModifier.CONST)
                            .initializer("%LU", message.id)
                            .build()
                    )
                    .addProperty(
                        PropertySpec
                            .builder("CRC_EXTRA", Byte::class)
                            .addModifiers(KModifier.CONST)
                            .initializer("%L", message.crcExtra)
                            .build()
                    )
                    .build()
            ).addFunction(
                FunSpec
                    .builder("toBytes")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(ByteArray::class)
                    .addStatement(
                        "val buffer = %T.allocate(255).order(%M)",
                        ByteBuffer::class,
                        MemberName(ByteOrder::class.asClassName(), "LITTLE_ENDIAN")
                    )
                    .apply {
                        message.orderedFields.forEach { field ->
                            addStatement(
                                format = "${field.propertyName}?.let(buffer::%M)",
                                MemberName(
                                    packageName = "com.mmoczkowski.mavlink.util",
                                    simpleName = "putNext"
                                )
                            )
                        }
                    }
                    .addStatement("return buffer.array().copyOf(buffer.position())")
                    .build()
            )
            .addKdoc(
                message.description.replace("%", "%%"),
            )

        val fileSpec = fileSpecBuilder
            .addImport("kotlin.math", "min")
            .addImport("com.mmoczkowski.mavlink.util", "accumulate")
            .addType(classBuilder.build())
            .build()

        fileSpec.writeTo(codeGenerator, false)
    }
}
