![Plushie Friends Title](https://cdn.modrinth.com/data/cached_images/f30e6befd7a6ccea12dc6fe4cb34b09f3545054c.png)

**Plushie Friends** is a Fabric and NeoForge mod that lets you turn players into cute, collectible 3D plushies to decorate your world!

*   **Skins Don't Change:** They work just like vanilla player heads. The plushie grabs a player's skin the moment it's made and locks it in. If that player updates their skin later, your plushie stays exactly the same as a neat little time capsule.
*   **Cute 3D Models:** The plushies have detailed little heads, bodies, arms, and legs that automatically turn to face whichever way you place them down.
*   **Name Tags:** Hovering over a plushie in your inventory shows the owner's name in a clean, colored text so you don't lose track of who is who.
*   **Great for Modpacks:** Fully supports datapacks, making it easy for pack creators add player plushies into loot chests. You can find a guide [here](https://wiki.thesalad.bar/mods/plushie-friends/datapack/).

***

### How to Get Them

Either by using a custom datapack, more info [here](https://wiki.thesalad.bar/mods/plushie-friends/datapack/)

or by using commands, you can spawn them in using this command:

For Minecraft 1.21.1:

```text
/give @p plushie_friends:plushie[profile={name:"PLAYER_NAME"}]
```

For Minecraft 1.20.1:

```text
/give @p plushie_friends:plushie{PlushieOwner:"PLAYER NAME"}
```

### Build

The 1.21.1 Fabric and NeoForge targets share the same source tree through Stonecutter:

```bash
./gradlew :1.21.1-fabric:build
./gradlew :1.21.1-neoforge:build
```

The built JAR files are in each target's `build/libs` directory.
