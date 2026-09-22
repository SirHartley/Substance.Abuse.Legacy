# Substance.Abuse (Legacy)

![Substance.Abuse banner](https://raw.githubusercontent.com/SirHartley/Substance.Abuse.Legacy/master/_substanceAbuse.png)

A [Starsector](https://fractalsoftworks.com/) mod that lets you distribute drinks to your crew for fleet-wide benefits. Ten beverages offer different effects, but keeping the fleet supplied matters: prolonged use builds dependence, and running dry brings withdrawal.

**This legacy release:** Substance.Abuse **1.1.d** for **Starsector 0.98a**. For ongoing development, see the [main Substance.Abuse repository](https://github.com/SirHartley/Substance.Abuse).

## What it adds

- **Ten consumable drinks**, each with its own combat or campaign effect and withdrawal penalty.
- **Automatic fleet consumption** while a drink is being distributed. Each drink tracks dependence separately.
- **Breweries, recipes, and recipe books** for producing drinks at colonies.
- **Faction alcohol and economy integration**, with source code for modders to inspect.

The drinks range from **Phoenix Stout** (armor regeneration and weapon range) and **Absynth** (lower weapon and fighter OP costs) to **Phaged Darjeeling** (shield and phase benefits). **Hangover Cure** helps manage effect buildup and withdrawal. See each item's in-game tooltip for its full effects and current status.

## Download and install

1. Install [LunaLib](https://fractalsoftworks.com/forum/index.php?topic=25658) and enable it in the Starsector launcher. LunaLib is a required dependency.
2. Download the [Substance.Abuse Legacy release](https://github.com/SirHartley/Substance.Abuse.Legacy/releases).
3. Extract it into your Starsector `mods` directory, with `mod_info.json` directly inside the mod's folder.
4. Enable **Substance.Abuse** in the launcher.

Check the release's listed game version before installing it. This repository's `mod_info.json` declares Starsector 0.98a and mod version 1.1.d.

## How to play

1. Buy or otherwise obtain a drink and keep it in your fleet's cargo.
2. Right-click it in cargo to start distributing it. The crew consumes it over time, and its effects apply to your fleet.
3. Right-click it again to stop distribution. Watch the item's tooltip and your stock: stopping or running out after dependence builds can trigger withdrawal. Distributing too much at once can cause a blackout.

To make your own supply, get a drink's recipe by raiding a market with a brewery, then install the recipe in a brewery on one of your colonies. Right-clicking recipes lets you create or add to a **recipe book**; install the book in a brewery to split production among its recipes.

## Settings

Change consumption, dependence gain, the blackout threshold, Hangover Cure strength, and automatic OP adjustment through the **LunaLib settings menu**. The setting definitions are in [`data/config/LunaSettings.csv`](data/config/LunaSettings.csv).

## Credits and links

- Concept, code, and implementation: **SirHartley**
- Item sprites: purchased and modified assets from [David's Unity Asset Store packs](https://assetstore.unity.com/publishers/13841)
- Brewery industry image: commissioned from [Anton Juntunen](https://www.artstation.com/antonjuntunen)
- Special thanks: **Avanitia** and **SilverLight**

See the [changelog](Changelog) for version history, the [forum thread](https://fractalsoftworks.com/forum/index.php?topic=24378.0) for discussion, or [open an issue](https://github.com/SirHartley/Substance.Abuse.Legacy/issues) for a repository-specific problem.
