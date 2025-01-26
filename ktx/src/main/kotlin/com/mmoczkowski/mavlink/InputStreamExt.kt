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

package com.mmoczkowski.mavlink

import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

fun InputStream.asMavLinkFlow(vararg protocols: MavLinkProtocol): Flow<MavLinkParser.Result> = flow {
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
