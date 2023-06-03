package com.mmoczkowski.mavlink

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

fun Flow<Byte>.mapToMavLink(vararg protocols: MavLinkProtocol): Flow<MavLinkFrame> = flow {
    val parser = MavLinkParser(*protocols)
    this@mapToMavLink.collectLatest { byte ->
        val frame = parser.parseNextByte(byte)
        if(frame != null) {
            emit(frame)
        }
    }
}
