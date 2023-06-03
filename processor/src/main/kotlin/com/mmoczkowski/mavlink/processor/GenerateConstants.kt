package com.mmoczkowski.mavlink.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.mmoczkowski.mavlink.processor.definition.MavLinkProtocolDefinition
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

internal fun MavLinkProtocolDefinition.generateConstants(codeGenerator: CodeGenerator, packageName: String) {
    enums.asIterable().forEach { enum ->
        val className = ClassName(packageName, enum.name)


        val fileSpec = FileSpec.builder(className)
            .apply {
                if(enum.description != null) {
                    addFileComment(enum.description)
                }
            }
            .addType(
                TypeSpec
                    .objectBuilder(className)
                    .apply {
                        enum.entries.forEach { enumEntry ->
                            addProperty(
                                PropertySpec.builder(enumEntry.name, UInt::class)
                                    .addModifiers(KModifier.CONST)
                                    .initializer("%Lu", enumEntry.value)
                                    .build()
                            )

                        }
                    }
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, false)
    }
}

