# Tick-Aligned Position

A client-side Forge mod for Minecraft 1.8.9. It renders your player at the exact position from the
latest physics tick instead of blending between the last two ticks. Movement looks choppy (20 updates
per second), but the frame rate, mouse look and everything else stays smooth.

Press **H** in-game to toggle it (rebind it under Options > Controls > Tick-Aligned Position). A chat
message shows whether it's on or off. It starts off each time the game launches.

## How it works

Every frame, vanilla draws the player and camera at `prev + (current - prev) * partialTicks`. Just
before each frame is drawn, the mod sets the player's previous-tick position fields (`prevPos*` for the
camera, `lastTickPos*` for the player model and render origin) to the current position, so that formula
gives exactly the current tick position. After the frame, it puts the real values back. See
`PositionSnapper.java` for details.

Scope: only your own player's position. Other entities, view bobbing, rotation and animations are
unchanged.

## Building

Requirements:

- **JDK 17 or newer** to run Gradle. (Gradle 8.8, used here, supports JDK 17 through 22.)
- **Java 8** to compile the mod. You don't need to install it yourself: Gradle downloads a Java 8
  toolchain automatically the first time if it can't find one.

Then, from the repository root:

```sh
./gradlew build        # macOS / Linux
gradlew.bat build      # Windows
```

The first build takes a few minutes, since it downloads and remaps Minecraft and Forge. The mod jar
ends up at:

```
build/libs/tickaligned-1.0.0.jar
```

(`build/intermediates/` holds a dev-mapped jar that won't load in a normal install. Ignore it.)

If Gradle picks the wrong JDK, set `JAVA_HOME` to a JDK 17+ install before running it.

## Installing

1. Install Forge for 1.8.9 (11.15.1.2318 or newer).
2. Copy `tickaligned-1.0.0.jar` into your `.minecraft/mods` folder.
3. Launch the Forge 1.8.9 profile.

## Developing

- `./gradlew runClient` starts a dev copy of Minecraft with the mod loaded (offline account, so
  singleplayer only).
- Open the folder in IntelliJ IDEA as a Gradle project. Set the Gradle JVM to JDK 17+ under
  Settings > Build Tools > Gradle.
- Bump the version in both `gradle.properties` and the `@Mod` annotation in `TickAlignedMod.java`.

Build setup is based on [nea89o/Forge1.8.9Template](https://github.com/nea89o/Forge1.8.9Template).
