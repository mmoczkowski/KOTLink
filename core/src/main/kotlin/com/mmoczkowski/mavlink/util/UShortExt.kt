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

package com.mmoczkowski.mavlink.util

infix fun UShort.accumulate(byte: UByte): UShort {
    var tmp: UByte = byte xor (this and 0xffu).toUByte()
    tmp = tmp xor (tmp.toInt() shl 4).toUByte()

    return ((this.toInt() shr 8) xor (tmp.toInt() shl 8) xor (tmp.toInt() shl 3) xor (tmp.toInt() shr 4)).toUShort()
}

infix fun UShort.accumulate(str: String): UShort = str.fold(this) { crc, char ->
    crc accumulate char.code.toUByte()
}
