package com.mmoczkowski.mavlink

import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

fun InputStream.asMavLinkFlow(vararg protocols: MavLinkProtocol): Flow<MavLinkFrame> = flow {
    val parser = MavLinkParser(*protocols)
    withContext(Dispatchers.IO) {
        while (isActive) {
            if (available() > 0) {
                val byte = read().toByte()
                val frame = parser.parseNextByte(byte)
                if(frame != null) {
                    emit(frame)
                }
            }
            yield()
        }
    }
}
