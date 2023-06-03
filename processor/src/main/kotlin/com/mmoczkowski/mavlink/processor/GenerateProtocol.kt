package com.mmoczkowski.mavlink.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.mmoczkowski.mavlink.MavLinkMessage
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
        "\t${message.id}u -> ${message.className}.fromBytes(payload)"
    }

    val crcExtraCodeBlock = messages.joinToString(
        separator = "\n",
        prefix = "return when(messageId) {\n",
        postfix = "\nelse -> null\n}"
    ) { message ->
        "\t${message.id}u -> ${message.crcExtra}"
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
                        .returns(MavLinkMessage::class.asTypeName().copy(nullable = true))
                        .addCode(fromBytesCodeBlock)
                        .build()
                )
                .addFunction(
                    FunSpec
                        .builder("getCrcExtra")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("messageId", UInt::class)
                        .returns(Byte::class.asTypeName().copy(nullable = true))
                        .addCode(crcExtraCodeBlock)
                        .build()
                )
                .build()
        )

        .build().writeTo(codeGenerator, false)
}
