package com.mmoczkowski.mavlink

class InvalidChecksumException(expected: UShort, actual: UShort) :
    Exception("Invalid checksum. Expected: $expected, actual: $actual")
