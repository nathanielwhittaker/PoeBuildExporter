package poebuildexporter.code.ItemData;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import poebuildexporter.code.PropertiesManagerCore;
import poebuildexporter.code.build.Build;
import poebuildexporter.code.poeapi.Flags;
import poebuildexporter.code.poeapi.WeaponBaseStats;
import poebuildexporter.code.poeapi.WeaponBaseStatsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class Item implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Item.class);

	/**
	 * 
	 */
	@Serial
    private static final long serialVersionUID = 2456L;
	public String name;
    public List<Stat> stats;
    public String type;
    public String text;
    public Flags flags;
    public String baseType;
    public String rarity;
    public int es;
    public int evasion;
    public int armour;
    public int edps;
    public int pdps;
    public int links;
    public int ilvl;
    public Boolean corrupted;
    public int gemLevel;
    public int localBaseCrit;
    public int quality;

    public Integer id;

    //DO NOT USE, ONLY MEANT FOR DESERIALIZATION.
    public Item() {

    }

public static final Map<String, ItemDefenseSetter> defenseSetterMap = Map.of(
            "energy shield:",       (i, v) -> i.es = v,
            "evasion:",             (i, v) -> i.evasion = v,
            "armour:",              (i, v) -> i.armour = v
    );

    private record ParsedStat(String stat, String searchKey, double numericRoll) {}
    private record StatResolution(String id, double roll) {}

    public Item(String name, String rarity, String type, List<Stat> stats) {
		this(name, rarity, type, null, stats, -1, -1, -1, -1, -1, -1, -1, null, -1, -1);
	}
	
	//this constructor got really out of hand fast...
	public Item(String name, String rarity, String type, String baseType, List<Stat> stats, int es, int evasion, int armour, int edps, int pdps, int ilvl, int links, Boolean corrupted, int gemLevel, int localBaseCrit) {
		this.name = name;
		this.type = type;
		this.rarity = rarity;
		this.stats = stats;
		this.es = es;
		this.evasion = evasion;
		this.armour = armour;
		this.baseType = baseType;
		this.edps = edps;
		this.pdps = pdps;
		this.ilvl = ilvl;
		this.links = links;
		this.corrupted = corrupted;
		this.gemLevel = gemLevel;
		this.localBaseCrit = localBaseCrit;
	}
	
	public Item(String name, String rarity, String baseType) {
		this.name = name;
		this.rarity = rarity;
		this.baseType = baseType;
		this.stats  = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public List<Stat> getStats() {
		return stats;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String getText() {
		return text;
	}
	
	public Flags getFlags() {
		return flags;
	}
	
	public void setFlags(Flags flags) {
		this.flags = flags;
	}
	
	public String getRarity() {
		return rarity;
	}
	
	public int getEs() {
		return es;
	}
	
	public int getEvasion() {
		return evasion;
	}
	
	public int getArmour() {
		return armour;
	}
	
	public String getBaseType() {
		return baseType;
	}
	
	public int getEDPS() {
		return edps;
	}
	
	public int getPDPS() {
		return pdps;
	}
	
	public int getIlvl() {
		return ilvl;
	}
	
	public int getLinks() {
		return links;
	}
	
	public Boolean getCorrupted() {
		return corrupted;
	}
	
	public int getGemLevel() {
		return gemLevel;
	}
	
	public int getLocalCrit() {
		return localBaseCrit;
	}


    /**
     * Honestly, good luck following this method. Still meaning to clean this one up.
     * @param item The raw item data in POB item data format. This method expected it in POB format.
     *             You SHOULD be able to get this via ctrl + c in game while overing over
     *             an item as an example String.
     * @param build The build that it's for. Used mostly for getting the correct API and item/stat data.
     * @return The properly (hopefully) de-serialized item.
     */
    public static Item fromStringForBuild(String item, Build build) {
        String[] statLines = item.split("\n");
        String rarity = statLines[0].substring("Rarity: ".length());
        String baseType = null;
        String name = statLines[1];
        List<String> extraStatsToIgnoreBecauseTheyAreLocal = List.of(
                "to maximum energy shield",
                "increased energy shield",
                "increased armour, evasion and energy shield",
                "to evasion rating",
                "increased evasion rating",
                "to armour",
                "increased armour"

        );
        int startStatParseIndex = 2;
        if (!rarity.equalsIgnoreCase("MAGIC")) {
            startStatParseIndex = 3;
            baseType = statLines[2];
            if (rarity.equalsIgnoreCase("UNIQUE") && !PropertiesManagerCore.getUniqueItemWhitelist().contains(name)) {
                return new Item(name, rarity, baseType);
            }
        } else {
            String magicNameLower = name.toLowerCase();
            baseType = build.getAllBaseTypes().stream()
                    .map(Item::getBaseType)
                    .filter(bt -> bt != null && magicNameLower.contains(bt.toLowerCase()))
                    .max(Comparator.comparingInt(String::length))
                    .orElse(null);
            if (baseType == null) {
                log.warn("Could not determine base type for magic item '{}'", name);
            }
        }

        Item finalItem = new Item(name, rarity, baseType);
        List<Stat> allStatsOnThisItem = new ArrayList<>();

        // Pre-scan: collect socketed runes and find "Implicits: N"
        int implicitsStartIndex = -1;
        int implicitCount = 0;
        Set<String> runeStatPatterns = new HashSet<>();
        Map<String, List<String>> runeStatsMap = PropertiesManagerCore.getRuneStatsMap();
        for (int i = startStatParseIndex; i < statLines.length; i++) {
            String lineLower = statLines[i].toLowerCase();
            if (lineLower.startsWith("rune:")) {
                String runeName = statLines[i].substring("Rune:".length()).trim().toLowerCase();
                List<String> patterns = runeStatsMap.get(runeName);
                if (patterns != null) {
                    runeStatPatterns.addAll(patterns);
                }
            }
            if (lineLower.startsWith("implicits:")) {
                try {
                    implicitCount = Integer.parseInt(statLines[i].split(":")[1].trim());
                } catch (NumberFormatException ignored) {}
                implicitsStartIndex = i + 1;
                break;
            }
        }

        // Weapon DPS tracking
        boolean isWeapon = computeIsWeapon(baseType, build);

        Map<String, Double> weaponDmg = new LinkedHashMap<>();

        Set<Integer> skipLines = new HashSet<>();
        for (int i = startStatParseIndex; i < statLines.length; i++) {
            if (skipLines.contains(i)) {
                continue;
            }
            if (applyStructuredField(statLines[i], finalItem)) {
                continue;
            }

            ParsedStat ps = parseStat(statLines[i]);

            if (isWeapon && Stat.weaponDpsStatNames.contains(ps.stat().toLowerCase().trim())) {
                weaponDmg.merge(ps.stat().toLowerCase().trim(), ps.numericRoll(), Double::sum);
                continue;
            }

            if (extraStatsToIgnoreBecauseTheyAreLocal.contains(ps.stat().toLowerCase().trim())) {
                continue;
            }

            if (implicitsStartIndex >= 0 && i >= implicitsStartIndex && i < implicitsStartIndex + implicitCount) {
                if (!runeStatPatterns.isEmpty()) {
                    String finalKey = ps.searchKey();
                    if (runeStatPatterns.stream().anyMatch(p -> finalKey.contains(p.toLowerCase()))) {
                        continue;
                    }
                }
                allStatsOnThisItem.add(resolveImplicitStat(ps, build));
            } else {
                StatResolution resolution = resolveStatId(ps.searchKey(), ps.numericRoll(), i, statLines, skipLines, build);
                if (resolution.id() == null) {
                    log.warn("No trade stat ID found for '{}' on item '{}'", ps.searchKey(), name);
                }
                allStatsOnThisItem.add(new Stat(ps.stat(), resolution.id(), resolution.roll()));
            }
        }

        if (isWeapon) {
            computeAndApplyWeaponDps(finalItem, baseType, build, weaponDmg);
        }

        finalItem.setStats(collapseStats(allStatsOnThisItem));
        return finalItem;
    }

    private static boolean computeIsWeapon(String baseType, Build build) {
        if (baseType != null) {
            String cat = build.getBaseTypeCategoryProperties().getCategory(baseType);
            return cat != null && cat.startsWith("weapon.");
        }
        return false;
    }

    private static boolean applyStructuredField(String line, Item item) {
        if (line.toLowerCase().startsWith("item level:")) {
            try {
                item.ilvl = Integer.parseInt(line.split(":")[1].trim());
            } catch (NumberFormatException ignored) {}
            return true;
        }
        if (line.toLowerCase().startsWith("sockets:")) {
            String sockStr = line.substring(line.indexOf(':') + 1).trim();
            int maxLinks = 0;
            for (String group : sockStr.split("\\s+")) {
                maxLinks = Math.max(maxLinks, group.split("-").length);
            }
            item.links = maxLinks;
            return true;
        }
        if (line.toLowerCase().startsWith("level:")) {
            try {
                item.gemLevel = Integer.parseInt(line.split(":")[1].trim());
            } catch (NumberFormatException ignored) {}
            return true;
        }
        if (line.trim().equalsIgnoreCase("corrupted")) {
            item.corrupted = true;
            return true;
        }
        if (line.toLowerCase().startsWith("implicits:")) {
            return true;
        }
        if (line.toLowerCase().startsWith("bonded:")) {
            return true;
        }
        if (line.toLowerCase().startsWith("quality:")) {
            try {
                item.quality = Integer.parseInt(line.split(":")[1].trim());
            } catch (NumberFormatException ignored) {}
            return true;
        }
        if (line.length() > 4 && Stat.statStringsToIgnoreWhenParsing.contains(line.toLowerCase().substring(0, 4))) {
            return true;
        }
        if (Stat.isLocalDefenseStat(line)) {
            String lineLower = line.toLowerCase().trim();
            for (Map.Entry<String, ItemDefenseSetter> entry : defenseSetterMap.entrySet()) {
                if (lineLower.startsWith(entry.getKey())) {
                    Matcher defMat = Pattern.compile("\\d+").matcher(line);
                    if (defMat.find()) {
                        entry.getValue().setDefense(item, Integer.parseInt(defMat.group()));
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

    private static ParsedStat parseStat(String rawLine) {
        String line = rawLine;
        if (line.toLowerCase().startsWith("adds ")) {
            line = line.substring("adds ".length()).trim();
        }

        Pattern flatDamagePattern = Pattern.compile(
                "([+-]?\\d+(?:\\.\\d+)?)\\s*to\\s*([+-]?\\d+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);
        Matcher flatDamagePatternMatcher = flatDamagePattern.matcher(line);
        if (flatDamagePatternMatcher.find()) {
            double num1 = Double.parseDouble(flatDamagePatternMatcher.group(1));
            double num2 = Double.parseDouble(flatDamagePatternMatcher.group(2));
            line = Math.round((num1 + num2) / 2.0) + line.substring(flatDamagePatternMatcher.end()).trim();
        }

        Pattern pattern = Pattern.compile("[+-]?\\d+(\\.\\d+)?%?\\s?");
        Matcher matcher = pattern.matcher(line);
        String stat = line;
        String searchKey = line.toLowerCase().trim();
        double numericRoll = -1;
        if (matcher.find()) {
            String matched = matcher.group();
            String prefix = line.substring(0, matcher.start()).trim();
            String suffix = line.substring(matcher.end()).trim();
            boolean hasPercent = matched.contains("%");
            boolean hasSign = matched.charAt(0) == '+' || matched.charAt(0) == '-';
            double rawRoll = Double.parseDouble(matched.replaceAll("[^0-9.]", ""));
            stat = suffix;
            searchKey = ((prefix.isEmpty() ? "" : prefix + " ")
                    + (hasSign ? matched.charAt(0) : "") + "#" + (hasPercent ? "%" : "")
                    + (suffix.isEmpty() ? "" : " " + suffix)).toLowerCase().trim();
            boolean excluded = PropertiesManagerCore.getRollDeltaExclusions()
                    .stream().anyMatch(searchKey::contains);
            numericRoll = excluded ? (int) rawRoll
                    : (int)(rawRoll * PropertiesManagerCore.getStatRollDelta());
        }
        return new ParsedStat(stat, searchKey, numericRoll);
    }

    private static Stat resolveImplicitStat(ParsedStat ps, Build build) {
        String implicitId = build.getIdFromStatText(ps.searchKey(), "implicit");
        String enchantId  = build.getIdFromStatText(ps.searchKey(), "enchant");
        List<String> ids = new ArrayList<>();
        if (implicitId != null) {
            ids.add(implicitId);
        }
        if (enchantId != null && !ids.contains(enchantId)) {
            ids.add(enchantId);
        }
        String primaryId = ids.isEmpty() ? build.getIdFromStatText(ps.searchKey()) : ids.get(0);
        Stat newStat = new Stat(ps.stat(), primaryId, ps.numericRoll());
        if (ids.size() >= 2) {
            newStat.setAlternateId(ids.get(1));
        }
        return newStat;
    }

    private static StatResolution resolveStatId(String searchKey, double numericRoll, int lineIdx, String[] lines, Set<Integer> skipLines, Build build) {
        if (lineIdx + 2 < lines.length && !skipLines.contains(lineIdx + 1) && !skipLines.contains(lineIdx + 2)) {
            String threeLineKey = searchKey + "\n"
                    + normalizeLineToSearchKey(lines[lineIdx + 1]) + "\n"
                    + normalizeLineToSearchKey(lines[lineIdx + 2]);
            String threeLineId = build.getIdFromStatText(threeLineKey);
            if (threeLineId != null) {
                skipLines.add(lineIdx + 1);
                skipLines.add(lineIdx + 2);
                return new StatResolution(threeLineId, numericRoll);
            }
        }
        if (lineIdx + 1 < lines.length && !skipLines.contains(lineIdx + 1)) {
            String twoLineKey = searchKey + "\n" + normalizeLineToSearchKey(lines[lineIdx + 1]);
            String twoLineId = build.getIdFromStatText(twoLineKey);
            if (twoLineId != null) {
                skipLines.add(lineIdx + 1);
                return new StatResolution(twoLineId, numericRoll);
            }
        }
        String id = build.getIdFromStatText(searchKey);
        if (id != null) {
            return new StatResolution(id, numericRoll);
        }
        if (searchKey.contains("reduced")) {
            String increasedKey = searchKey.replaceFirst("reduced", "increased");
            String increasedId = build.getIdFromStatText(increasedKey);
            if (increasedId != null) {
                return new StatResolution(increasedId, -numericRoll);
            }
        }
        return new StatResolution(null, numericRoll);
    }

    private static List<Stat> collapseStats(List<Stat> stats) {
        Map<String, Stat> byId = new LinkedHashMap<>();
        List<Stat> unmatched = new ArrayList<>();
        for (Stat s : stats) {
            if (s.getId() == null) {
                unmatched.add(s);
                continue;
            }
            byId.merge(s.getId(), s, (existing, next) ->
                    new Stat(existing.getText(), existing.getId(), existing.getRoll() + next.getRoll()));
        }
        List<Stat> collapsed = new ArrayList<>(byId.values());
        collapsed.addAll(unmatched);
        return collapsed;
    }

    private static void computeAndApplyWeaponDps(Item item, String baseType, Build build, Map<String, Double> weaponDmg) {
        WeaponBaseStatsProperties baseStatsProps = build.getWeaponBaseStats();
        if (baseStatsProps == null) {
            return;
        }
        WeaponBaseStats baseStats = baseStatsProps.getStats(baseType);
        if (baseStats == null) {
            return;
        }
        double physFlatDmg = weaponDmg.getOrDefault("physical damage",           0.0);
        double physIncrPct = weaponDmg.getOrDefault("increased physical damage", 0.0);
        double apsIncrPct  = weaponDmg.getOrDefault("increased attack speed",    0.0);
        double fireDmg     = weaponDmg.getOrDefault("fire damage",               0.0);
        double coldDmg     = weaponDmg.getOrDefault("cold damage",               0.0);
        double lightDmg    = weaponDmg.getOrDefault("lightning damage",          0.0);
        double finalAps    = baseStats.aps * (1.0 + apsIncrPct / 100.0);
        double basePhysAvg = (baseStats.physMin + baseStats.physMax) / 2.0;
        double totalPhys   = (basePhysAvg + physFlatDmg) * (1.0 + (item.quality + physIncrPct) / 100.0);
        item.pdps = (int) Math.floor(totalPhys * finalAps);
        item.edps = (int) Math.floor((fireDmg + coldDmg + lightDmg) * finalAps);
    }

    private static String normalizeLineToSearchKey(String rawLine) {
        return parseStat(rawLine).searchKey();
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

	@Override 
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(StringUtils.isNotBlank(name) ) {
			builder.append(name);
		} else if(StringUtils.isNotBlank(baseType)) {
			builder.append(baseType);
		} else if(StringUtils.isNotBlank(type)) {
			builder.append(type);
		} else {
			builder.append("Missing String");
		}
		
		if(CollectionUtils.isNotEmpty(stats)) {
			builder.append(" ");
			for(Stat stat : stats) {
				builder.append(stat.getText());
				builder.append(" ");
				if(stat.getRoll() != -1) {
					builder.append(stat.getRoll());
					builder.append(" ");
				}
			}
		}
		return builder.toString();
	}

}
