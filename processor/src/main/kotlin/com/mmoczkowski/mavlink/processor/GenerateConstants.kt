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

