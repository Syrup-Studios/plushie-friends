# Plushie Friends Data Pack Guide

This guide explains how to create a data pack that adds predefined Plushie Friends plushies to loot tables.

!!! warning "Use the current namespace"

    Always use `plushie_friends` for the Plushie Friends item and loot function.

    Do not use the old `plushie-friends` namespace. A hyphen and an underscore are not interchangeable in a Minecraft resource location.

The data-pack format and loot-table layout are different in Minecraft 1.20.1 and 1.21.1. Use the section for your Minecraft version.

## Before you start

A data pack is a folder that contains a `pack.mcmeta` file and a `data` folder. This guide uses:

- `my_custom_plushies` as the data-pack folder name.
- `example` as the custom namespace.
- `playername` as the plushie ID.

You can change `example` to your own namespace. Use lowercase letters, numbers, underscores, periods, or hyphens. Keep the same namespace in the folder path and in each resource ID.

For both Minecraft versions, a plushie definition goes in:

```text
data/<namespace>/plushies/<plushie_id>.json
```

For example, the resource ID `example:playername` points to:

```text
data/example/plushies/playername.json
```

The Plushie Friends item ID is `plushie_friends:plushie`. The custom loot function is `plushie_friends:set_plushie`.

## Minecraft 1.20.1

Minecraft 1.20.1 uses `pack_format` 15. Its loot-table folder is named `loot_tables`, with a final `s`.

### Complete folder tree

```text
my_custom_plushies/
├── pack.mcmeta
└── data/
    ├── example/
    │   ├── loot_tables/
    │   │   └── pools/
    │   │       └── all_plushies.json
    │   └── plushies/
    │       └── playername.json
    └── minecraft/
        └── loot_tables/
            └── chests/
                └── abandoned_mineshaft.json
```

### 1. Create `pack.mcmeta`

Create `my_custom_plushies/pack.mcmeta`:

```json
{
  "pack": {
    "pack_format": 15,
    "description": "Custom Plushie Friends plushies for Minecraft 1.20.1"
  }
}
```

### 2. Define the plushie

Create `data/example/plushies/playername.json`:

```json
{
  "owner_name": "PLAYERNAME",
  "lore": [
    "First lore line",
    "Second lore line"
  ]
}
```

`owner_name` is the Minecraft player name that supplies the plushie's skin. Each string in `lore` is one line in the item tooltip.

### 3. Create the plushie loot table

Create `data/example/loot_tables/pools/all_plushies.json`:

```json
{
  "type": "minecraft:generic",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "plushie_friends:plushie",
          "functions": [
            {
              "function": "plushie_friends:set_plushie",
              "id": "example:playername"
            }
          ]
        }
      ]
    }
  ]
}
```

The complete item entry that creates the plushie is:

```json
{
  "type": "minecraft:item",
  "name": "plushie_friends:plushie",
  "functions": [
    {
      "function": "plushie_friends:set_plushie",
      "id": "example:playername"
    }
  ]
}
```

The `id` value `example:playername` points to `data/example/plushies/playername.json`.

### 4. Use the plushie loot table

This example adds the custom loot table to abandoned mineshaft chests. Create `data/minecraft/loot_tables/chests/abandoned_mineshaft.json`:

```json
{
  "type": "minecraft:chest",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:loot_table",
          "name": "example:pools/all_plushies"
        }
      ]
    }
  ]
}
```

In Minecraft 1.20.1, a nested loot-table entry uses `name`:

```json
{
  "type": "minecraft:loot_table",
  "name": "example:pools/all_plushies"
}
```

This file replaces the full vanilla abandoned mineshaft chest loot table. If you want to keep the vanilla loot, copy its other pools into this file and add the plushie pool.

## Minecraft 1.21.1

Minecraft 1.21.1 uses `pack_format` 48. Its loot-table folder is named `loot_table`, without a final `s`.

### Complete folder tree

```text
my_custom_plushies/
├── pack.mcmeta
└── data/
    ├── example/
    │   ├── loot_table/
    │   │   └── pools/
    │   │       └── all_plushies.json
    │   └── plushies/
    │       └── playername.json
    └── minecraft/
        └── loot_table/
            └── chests/
                └── abandoned_mineshaft.json
```

### 1. Create `pack.mcmeta`

Create `my_custom_plushies/pack.mcmeta`:

```json
{
  "pack": {
    "pack_format": 48,
    "description": "Custom Plushie Friends plushies for Minecraft 1.21.1"
  }
}
```

### 2. Define the plushie

Create `data/example/plushies/playername.json`:

```json
{
  "owner_name": "PLAYERNAME",
  "lore": [
    "First lore line",
    "Second lore line"
  ]
}
```

`owner_name` is the Minecraft player name that supplies the plushie's skin. Each string in `lore` is one line in the item tooltip.

### 3. Create the plushie loot table

Create `data/example/loot_table/pools/all_plushies.json`:

```json
{
  "type": "minecraft:generic",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "plushie_friends:plushie",
          "functions": [
            {
              "function": "plushie_friends:set_plushie",
              "id": "example:playername"
            }
          ]
        }
      ]
    }
  ]
}
```

The complete item entry that creates the plushie is:

```json
{
  "type": "minecraft:item",
  "name": "plushie_friends:plushie",
  "functions": [
    {
      "function": "plushie_friends:set_plushie",
      "id": "example:playername"
    }
  ]
}
```

The `id` value `example:playername` points to `data/example/plushies/playername.json`.

### 4. Use the plushie loot table

This example adds the custom loot table to abandoned mineshaft chests. Create `data/minecraft/loot_table/chests/abandoned_mineshaft.json`:

```json
{
  "type": "minecraft:chest",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:loot_table",
          "value": "example:pools/all_plushies"
        }
      ]
    }
  ]
}
```

In Minecraft 1.21.1, a nested loot-table entry uses `value`:

```json
{
  "type": "minecraft:loot_table",
  "value": "example:pools/all_plushies"
}
```

This file replaces the full vanilla abandoned mineshaft chest loot table. If you want to keep the vanilla loot, copy its other pools into this file and add the plushie pool.

## Install and enable the data pack

1. Put the complete `my_custom_plushies` folder in `<world folder>/datapacks/`.
2. Start the world, or run `/reload` if the world is open.
3. Run `/datapack list enabled` to confirm that Minecraft enabled the pack.
4. Use `/loot give @s loot example:pools/all_plushies` to test the plushie loot table.

If Minecraft reports an error, check the game log. The most common causes are the wrong `pack_format`, the wrong singular or plural loot-table folder, an invalid JSON file, or use of the old `plushie-friends` namespace.
