package com.mmoczkowski.mavlink.processor.util

internal fun String.toCamelCase(capitalizeFirstLetter: Boolean): String {
    return split("_")
        .mapIndexed { index, chunk ->
            val lowercase = chunk.lowercase()
            if(!capitalizeFirstLetter && index == 0) {
                lowercase
            } else {
                lowercase.replaceFirstChar { char -> char.uppercase() }
            }
        }
        .joinToString("")
}
