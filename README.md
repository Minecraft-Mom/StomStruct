# StomStruct
 StomStruct is a library and extension for Minestom enabling interaction with structure block schematic files. It is intended to be used in situations where a server developer may want to load multi-block structures or schematics to memory, build these structures in an instance, or save a region of an instance to a permanent data store.

Features
---
 - Loading structures from a structure block file
 - Writing structures to a structure block file
 - Building structures at a given point in an instance
 - Creating structures from a region of space
 - In-game commands for easy use of structures

Usage
---
### Extension
The latest extension jarfile can be found under releases. The extension enables the structure commands, and shadows in the core library.

### Library
Gradle
```
repositories {
    // ...
    maven { url 'https://repo.spongepowered.org/maven' }
    maven { url 'https://jitpack.io' }
}

dependencies {
    // ...
    implementation 'com.github.ethan-spangler:StomStruct:master-SNAPSHOT'
}
```

To-Do
---
 [:heavy_check_mark:] ~~Apply block NBT when building a structure~~
 
 [:heavy_check_mark:] ~~Fix Structure Void blocks on structure creation~~
 
 [:x:] Entities when creating and loading structures
 
 [:x:] Add support for Sponge Schematics
 
 [:x:] Maybe: Load/Create structures from actual Structure Block
