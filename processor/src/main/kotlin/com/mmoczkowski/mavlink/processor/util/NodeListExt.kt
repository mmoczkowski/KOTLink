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
