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

package com.mmoczkowski.mavlink.processor.util

import org.w3c.dom.Node
import org.w3c.dom.NodeList

internal fun NodeList.asIterable(): Iterable<Node> = object : Iterable<Node> {
    override fun iterator() = this@asIterable.iterator()
}

internal fun NodeList.iterator(): Iterator<Node> = object : Iterator<Node> {
    var index = 0
    override fun hasNext() = index < length
    override fun next(): Node {
        if (!hasNext()) throw NoSuchElementException()
        return item(index++)
    }
}
