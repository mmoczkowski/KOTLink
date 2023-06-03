package com.mmoczkowski.mavlink

class UnsupportedMessageException(messageId: UInt): Exception("Unsupported message #$messageId")
