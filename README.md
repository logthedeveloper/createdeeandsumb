# Create: TweedleDee n' Tweedle Dumb

NeoForge 1.21.1 mod (mod id `tweedledeentweedledum`, package `com.logthedeveloper.tweedledeentweedledum`).
Two plushie blocks, **Tweedle Dee** and **Tweedle Dumb**. Place them, or wear them on your shoulders for effects.

## Build / run

Needs Java 21. In this folder:

    ./gradlew build        (Windows: gradlew.bat build)    -> build/libs/tweedledeentweedledum-1.0.0.jar
    ./gradlew runClient    test it in game

Open the folder in IntelliJ IDEA as a Gradle project and it sets everything up by itself.
Gradle downloads NeoForge, so the first run needs internet and takes a few minutes.
Versions live in `gradle.properties` (`neo_version=21.1.172`, bump it if you want a newer 1.21.1 build).

## Where YOUR models and textures go

Everything is under `src/main/resources/assets/tweedledeentweedledum/`. There are placeholder files in
every spot (a colour-coded test plush). Overwrite them with yours, keeping the file names:

| What                          | File                                         |
|-------------------------------|----------------------------------------------|
| Dee model (placed + shoulder) | `models/block/tweedle_dee.json`              |
| Dumb model (placed + shoulder)| `models/block/tweedle_dumb.json`             |
| Dee texture (skin)            | `textures/block/tweedle_dee.png`             |
| Dumb texture (skin)           | `textures/block/tweedle_dumb.png`            |

- The models point at `tweedledeentweedledum:block/tweedle_dee` and `.../tweedle_dumb` for their texture, so if
  you export from Blockbench either keep those texture paths or change them to match your PNG names.
- The model should face **north** (Blockbench's default front). The blockstate rotates it for the other directions.
- The item models (`models/item/tweedle_*.json`) just reuse the block models, and the shoulder renderer uses the
  item model too, so one model serves all three uses.
- The placeholder models are the chibi plush (standing). `tweedle_dee_sitting.json` / `tweedle_dumb_sitting.json`
  are sitting versions that are not used yet. To place the sitting pose, edit `blockstates/tweedle_*.json` and
  swap the model names to the `_sitting` ones.
- The collision/selection box is set in `PlushBlock.java` (`SHAPE_NORTH_SOUTH` / `SHAPE_EAST_WEST`). Change it if
  your model is a different size.

## How it plays

- Right click a block with a plush: **place** it (it faces you).
- Right click **in the air** (not pointing at a block) with a plush in hand: put it on your **shoulder**.
  The first one goes on the right shoulder, the second on the left. Two max.
- **Sneak + right click a placed plush with both hands empty**: pick it up onto your shoulder.
- **Sneak + right click in the air with both hands empty**: take a plush off (it goes back to your inventory).
- Breaking a placed plush drops it. If you die with plushes on your shoulders they drop (unless keepInventory).
- Other players see the plushes on your shoulders too.

## Config (effects!)

After the first launch: `config/tweedledeentweedledum-common.toml` (in a dev run: `run/config/`).

    effectsEnabled = true
    effectDurationTicks = 220
    showParticles = false
    showIcon = true

    [tweedle_dee]
        effects = ["minecraft:speed,0"]
    [tweedle_dumb]
        effects = ["minecraft:resistance,0"]

    [shoulder_render]
        scale = 0.45
        offsetX = 0.0
        offsetY = 0.0
        offsetZ = 0.0

An effect entry is `"effect_id,amplifier"` where amplifier 0 = level I, 1 = level II and so on.
Modded effects work too (`"somemod:some_effect,0"`). Put several entries in the list for several effects,
or `[]` for none. The effects are refreshed every second while the plush is on a shoulder and fade
`effectDurationTicks` after you take it off. Use `scale` and the `offset` values to line the plush up on
the shoulder to match your model.

## Notes

- The name starts with "Create:" but the mod does **not** depend on Create (so it also runs without it).
  If you want recipes that use Create items, add the Create dependency in `build.gradle` (there is a commented
  example) and `neoforge.mods.toml` (`src/main/templates/META-INF/`).
- There are no crafting recipes yet, the plushies are in the creative tab "Create: TweedleDee n' Tweedle Dumb".
