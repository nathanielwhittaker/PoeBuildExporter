# PoeBuildExporter

Reads Path of Exile builds from [pobb.in](https://pobb.in) URLs, queries the PoE trade API for each item in the build, and exports the results to a spreadsheet.

## Requirements

- Java 17+
- A valid `POESESSID` cookie from pathofexile.com

## Setup

On first launch, `properties/BaseUserConfig.properties` is created automatically from the example file. Fill in your `POESESSID` and league name(s) there, or set them directly from the GUI after launching.

## Running

Double-click `PoeBuildExporter.bat` to launch. If the app exits with an error the console will stay open — check `logs/poebuildexporter.log` for details.

## Building from source

Requires Maven.

```
mvn package
```

---

## Using the GUI

### Import via
Select how builds are loaded. The dropdown sits above the build input panel.

| Option | Description |
|---|---|
| Manual Entry | Type or paste one pobb.in URL per box. A new box appears automatically when the last one is filled. |
| File | Browse to a file containing build URLs. The file type is detected from the extension. |

#### File import formats

**Text (.txt)** — one URL per line. Lines starting with `#` are ignored.
```
# My builds
https://pobb.in/abc123
https://pobb.in/def456
```

**CSV (.csv)** — requires a `url` column header. An optional `name` column is supported.
```
name,url
My Pathfinder,https://pobb.in/abc123
Explode Jugg,https://pobb.in/def456
```

**Excel (.xlsx)** — same structure as CSV. First row is the header; the `url` column is extracted.

---

### Export format
Choose the output format. Currently supported:

| Option | Description |
|---|---|
| Excel Spreadsheet (.xlsx) | One sheet per build. Columns: Item Name, Base Type, Rarity, iLvl, Links, PDPS, EDPS, ES, Evasion, Armour, Total Listings, Trade URL. |

When a file-based export format is selected, a **Save to** row appears for choosing the output path.

### POESESSID / League
Your session ID and league names can be updated directly from the south panel. Click **Save** after editing. Changes are written to `BaseUserConfig.properties` and take effect immediately.

### Run
Loads each build, queries the trade API for every item (with a configurable delay between requests), then exports. A progress bar shows items completed out of total.

---

## Properties files

All properties files live in the `properties/` directory next to the jar.

### `BaseUserConfig.properties` *(gitignored — do not commit)*

Personal settings that vary per user.

| Key | Description |
|---|---|
| `POESESSID` | Your PoE session cookie. Found in browser dev tools under `pathofexile.com` cookies. |
| `league` | Active PoE 1 league name, e.g. `Settlers`. |
| `leaguePoE2` | Active PoE 2 league name, e.g. `Runes%20of%20Aldur`. URL-encode spaces. |

---

### `PathOfExileApiQuery.properties`

Trade API endpoints. These rarely need changing unless GGG updates their API paths.

| Key | Description |
|---|---|
| `statsApiLink` | PoE 1 stats endpoint. |
| `statsApiLinkPoE2` | PoE 2 stats endpoint. |
| `itemsApiLink` | PoE 1 items endpoint. |
| `itemsApiLinkPoE2` | PoE 2 items endpoint. |
| `tradeApiSearchUrl` | PoE 1 trade search POST URL. |
| `tradeApiSearchUrlPoE2` | PoE 2 trade search POST URL. |
| `tradeWebSearchUrl` | PoE 1 trade website URL (used to build hyperlinks in the export). |
| `tradeWebSearchUrlPoE2` | PoE 2 trade website URL. |
| `tradeApiRequestDelayMs` | Milliseconds to wait between trade API requests. Default `10000`. Lower values risk rate limiting. |

---

### `ItemParsing.properties`

Controls how item stats are parsed from build data.

| Key | Description |
|---|---|
| `statRollDelta` | Multiplier applied to a stat's roll when building the trade query minimum. E.g. `0.85` searches for rolls at least 85% of the build value. |
| `rollDeltaExclusions` | Comma-separated list of stat substrings that should not have the delta applied (matched by `contains`). E.g. `to level of`. |

---

### `UniqueItems.txt`

Plain text file, one unique item name per line. Lines starting with `#` are comments.

By default, unique items are queried by name and base type only. Items listed here have their stats fully parsed and included in the trade query, which produces more precise results at the cost of potentially fewer listings returned.

```
# Unique items listed here will have their stats fully parsed and included in trade queries.
Watcher's Eye
Mageblood
```

---

### `RuneStats.properties`

Maps rune names to lists of stat IDs they contribute. Used when parsing PoE 2 builds that include socketed runes.

Format: `runeName=stat.id.one,stat.id.two`
