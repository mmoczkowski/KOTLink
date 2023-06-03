package com.mmoczkowski.mavlink.processor

import com.google.devtools.ksp.processing.*

class MavLinkProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MavLinkProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            options = environment.options
        )
    }
}
