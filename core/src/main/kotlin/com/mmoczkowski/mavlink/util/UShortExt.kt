package com.mmoczkowski.mavlink.util

infix fun UShort.accumulate(byte: UByte): UShort {
    var tmp: UByte = byte xor (this and 0xffu).toUByte()
    tmp = tmp xor (tmp.toInt() shl 4).toUByte()

    return ((this.toInt() shr 8) xor (tmp.toInt() shl 8) xor (tmp.toInt() shl 3) xor (tmp.toInt() shr 4)).toUShort()
}

infix fun UShort.accumulate(str: String): UShort = str.fold(this) { crc, char ->
    crc accumulate char.code.toUByte()
}
