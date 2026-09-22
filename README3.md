# Substance.Abuse

<img src="_substanceAbuse.png" alt="Substance.Abuse icon" width="64" align="right">

A [Starsector](https://fractalsoftworks.com/) mod that adds 10 alcoholic drinks to the sector. Keep your fleet stocked and your crew gets bonuses. Run out, and they go into withdrawal. Drink too much at once, and you black out.

**Game version:** 0.98a | **Mod version:** 1.1.d | **Forum thread:** [fractalsoftworks.com/forum/index.php?topic=24378](https://fractalsoftworks.com/forum/index.php?topic=24378)

## Features

* **10 drinks**, each with a different fleet-wide bonus and withdrawal penalty
* **Addiction system:** your crew builds resistance to each drink separately, and drinks more as that resistance grows
* **Withdrawal:** run out of a drink and its bonus turns into a penalty that fades over time
* **Blackouts:** drink too much too quickly and you wake up alone. Your fleet stays where you left it
* **Brewery industry:** a rural industry that produces large stockpiles using its installed recipe
* **Recipes and Recipe Books:** raid a planet with a brewery to steal its recipe. Combine recipes into a Recipe Book to make them all at one brewery
* **Economy integration:** factions make and trade their own drinks, and your colonies import the drinks their breweries produce

## The Drinks

| Drink | Faction | Brewery | Effect |
|---|---|---|---|
| Phoenix Stout | Hegemony | Jangala | Limited armor regeneration, longer weapon range |
| Absynth | Tri-Tachyon | Port Tse | Lower OP costs for weapons and fighters |
| Tears of Ludd | Luddic Church | Gilead | More officer XP, better missiles |
| Prometheus Gift | Luddic Path | Chalcedon | Faster acceleration and speed, better maneuverability under sustained burn |
| Reapers Blood | Pirates | Qaras | Faster missiles and projectiles, better zero flux boost, more aggressive ships |
| Askonia Sunshine | Sindrian Diktat | Volturn | Limited hard flux dissipation through shields, less phase flux, lower fuel use |
| Kings Favour | Persean League | Fikenhild | Higher maximum CR and faster CR recovery, fewer ship and crew losses |
| Sweet Freedom | Independent | Ailmar | Lower tariffs, lower sensor profile, better fighters |
| Phaged Darjeeling | Independent | Asharu | Wider shield arc, faster shield unfolding and turning, less flux and CR decay while phased |
| Hangover Cure | Independent | Orthrus | Slower effect and resistance buildup, faster withdrawal recovery |

Hover over a drink in your cargo to see the exact numbers at your current addiction level.

## How It Works

1. Buy a drink and keep it in your cargo. Your fleet drinks it automatically.
2. Only the crew needed to run your ships (the skeleton crew) drinks. Roughly 1 unit supplies 25 crew for a month.
3. The longer they drink, the stronger the effect gets. Consumption also rises, up to twice the starting amount.
4. Run out and your fleet goes into withdrawal. Hangover Cure helps them recover faster.
5. Increase their intake too quickly and you black out. Your fleet stays behind and appears in your intel as a "Drunk Fleet". Fly back to rejoin it.

## Installation

1. Install [LunaLib](https://fractalsoftworks.com/forum/index.php?topic=25658) (required).
2. Download the latest release from the [Releases page](https://github.com/SirHartley/Substance.Abuse.Legacy/releases).
3. Extract the zip into your `Starsector/mods` folder.
4. Enable **Substance.Abuse** and **LunaLib** in the launcher.

You can add the mod to an existing save. It also supports Nexerelin random sector mode (breweries are placed on the largest market of each faction) and Version Checker.

## Settings

You can change all settings in game through the LunaLib menu:

| Setting | Default | Description |
|---|---|---|
| Crew per unit of alcohol, per month | 25 | Increase this to reduce consumption |
| Addiction gain per day | 0.005 | How quickly addiction builds |
| Monthly addiction gain limit | 1 | How much your crew can drink at once before you black out |
| Water Mult | 0.5 | How much Hangover Cure changes addiction gain and loss |
| OP adjustment | On | Automatically adjusts loadouts that exceed the OP limit during Absynth withdrawal |

## For Modders

Assign drinks to your faction (including vanilla drinks) from your mod plugin:

```java
FactionAlcoholHandler.setFactionAlcoholTypes("your_faction_id", AlcoholRepo.STOUT, AlcoholRepo.TEA);
```

Drink IDs are listed in `com.fs.starfarer.api.alcoholism.memory.AlcoholRepo`. The source code is in [`jars/src`](jars/src).

## Changelog

See [Changelog](Changelog).

## Credits

* **Concept, code, implementation:** SirHartley
* **Item sprites:** bought and modified, [Unity Asset Store](https://assetstore.unity.com/publishers/13841)
* **Industry image:** commission, [Anton Juntunen](https://www.artstation.com/antonjuntunen)
* **Special thanks:** Avanitia, SilverLight

## License

<img src="CC-license.png" alt="CC BY-NC-ND">

Licensed under [Creative Commons Attribution-NonCommercial-NoDerivatives 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/).

*Drink responsibly-ish.*
