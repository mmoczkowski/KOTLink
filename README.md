# KOTLink

KOTLink is an open-source MAVLink library developed in Kotlin. The project is structured in several modules and aims to
make MAVLink communication seamless and efficient for Kotlin developers. It provides a handy interface to work with
MAVLink protocols, parse MAVLink messages, and even auto-generate Kotlin data classes from MAVLink XML definitions.

![](https://github.com/mmoczkowski/KOTLink/assets/6339497/0b8dbd5b-0a5f-45aa-bbdf-7586d51b6fd6)

## Declaring dependencies

```kotlin
// The core of the library
implementation("com.mmoczkowski:kotlink-core:1.2.0")

// KTX extensions
implementation("com.mmoczkowski:kotlink-ktx:1.2.0")

// MAVLink Minimal protocol implementation
implementation("com.mmoczkowski:kotlink-protocol-minimal:1.2.0")

// MAVLink Common protocol implementation
implementation("com.mmoczkowski:kotlink-protocol-common:1.2.0")

// MAVLink ArduPilot Mega protocol implementation
implementation("com.mmoczkowski:kotlink-protocol-ardupilotmega:1.2.0")

// MAVLink code generation processor (only if you need to generate a protocol)
implementation("com.mmoczkowski:kotlink-processor:1.2.0")
```

## Sample Usage

Utilize the `MavLinkParser` class as demonstrated:

```kotlin
val parser = MavLinkParser(MinimalMavlinkProtocol, CommonMavLinkProtocol)
val bytes = ... // your data
val frames = parser.parseNextBytes(bytes)
frames.forEach { frame ->
    when (frame) {
        is MavLinkFrame.V1 -> // handle MavLink v1 frame
        is MavLinkFrame.V2 -> // handle MavLink v2 frame
    }
}
```

The Kotlin Flow extension `mapToMavLink` can be applied as follows:

```kotlin
val flow: Flow<Byte> = ...
val mavlinkFlow = flow.mapToMavLink(MinimalMavlinkProtocol, CommonMavLinkProtocol)
mavlinkFlow.collect { frame ->
    when (frame) {
        is MavLinkFrame.V1 -> // handle MavLink v1 frame
        is MavLinkFrame.V2 -> // handle MavLink v2 frame
    }
}
```

## Project Structure

The project is organized into the following modules:

- `:core`
- `:processor`
- `:protocol:minimal`
- `:protocol:common`
- `:protocol:ardupilotmega`
- `:ktx`

### :core

The `:core` module forms the backbone of KOTLink, providing essential MAVLink classes and interfaces, including:

- `MavLinkFrame`: This sealed class symbolizes a parsed MAVLink frame, containing two subclasses for `V1` and `V2` MAVLink versions.
- `MavLinkMessage`: A representative interface for a MAVLink message, encapsulating the function `toBytes(): ByteArray` for serialization of the message into a raw payload.
- `MavLinkProtocol`: This interface depicts a MAVLink subset, furnished with methods `fromBytes(messageId: UInt, payload: ByteArray): MavLinkMessage?` and `getCrcExtra(messageId: UInt): Byte?`.
- `MavLinkParser`: This class, essential for parsing sequences of bytes into a `MavLinkFrame` containing a `MavLinkMessage`, uses the provided MAVLink `XML` definitions for parsing.

### :processor

The `:processor` module serves as a KSP compiler plugin, translating MAVLink `XML` definitions into generated Kotlin data classes that inherit from the `MavLinkMessage` interface.

### :protocol

The `:protocol` module is further divided into three submodules - `:common`, `:minimal`, and `:ardupilotmega`. These modules consist of MAVLink classes auto-generated from their respective XML files: `common.xml`, `minimal.xml`, and `ardupilotmega.xml`.

### :ktx

The `:ktx` module adds functionality to the parser by offering Kotlin Flow extensions.
