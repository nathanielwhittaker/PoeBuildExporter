package poebuildexporter.code.build;

import java.util.List;
import java.util.function.Predicate;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.importer.BuildImporterResult;

//any future build types should extend this class and implement either poe1 or poe2 game version interface.
public abstract class Build implements PoeGameVersionConfig {

    public List<Stat> allPossibleStats;
    public List<Item> allPossibleBaseTypesWithLocalMods;
    protected List<Item> items;

    protected Build() {
        this.allPossibleStats = getTradeStats();
        this.allPossibleBaseTypesWithLocalMods = getTradeBaseTypesWithLocalMods();
    }

    public static Build of(BuildType type, BuildImporterResult importResult) {
        return type.create(importResult);
    }

    public abstract String getName();

    public List<Item> getItems() {
        return items;
    }

    public String getIdFromStatText(String statText) {
        return findBestId(statText, stat -> true);
    }

    public String getIdFromStatText(String statText, String idPrefix) {
        return findBestId(statText, stat -> stat.getId() != null && stat.getId().startsWith(idPrefix));
    }

    private String findBestId(String statText, Predicate<Stat> preFilter) {
        String lower = statText.toLowerCase().trim();
        if (lower.isEmpty()) return null;
        String suffix = suffixOf(lower);
        boolean hasSupportedBy = lower.contains("supported by");
        for (StatMatchStrategy strategy : StatMatchStrategy.values()) {
            Stat best = null;
            for (Stat stat : allPossibleStats) {
                if (preFilter.test(stat) && strategy.test(lower, suffix, stat)) {
                    // Band-aid: prevents suffix/contains strategies from matching "supported by X" support gem stats
                    // against item mods that merely share a suffix (e.g. flask "less duration" matching
                    // "supported by less duration"). Needs a proper solution once one is thought of.
                    // The root cause for the less duration modifier on flasks is due to that mod not existing
                    // as an independent stat from the poe 1 stat data api.
                    if (!hasSupportedBy && stat.getText().contains("supported by")) continue;
                    if (best == null || stat.getText().length() < best.getText().length()) best = stat;
                }
            }
            if (best != null) return best.getId();
        }
        return null;
    }

    private static String suffixOf(String normalizedKey) {
        int hashIdx = normalizedKey.indexOf('#');
        if (hashIdx < 0) return normalizedKey;
        int after = hashIdx + 1;
        if (after < normalizedKey.length() && normalizedKey.charAt(after) == '%') after++;
        if (after < normalizedKey.length() && normalizedKey.charAt(after) == ' ') after++;
        return normalizedKey.substring(after);
    }
}
