package poebuildexporter.code.poeapi;

public enum QueryStat {

    // armour_filters — threshold 1 (only include when value > 0)
    ENERGY_SHIELD(new QueryStatType<Integer>(null, "es",        QueryStatType.FilterSection.ARMOUR_FILTERS) {}),
    EVASION      (new QueryStatType<Integer>(null, "ev",        QueryStatType.FilterSection.ARMOUR_FILTERS) {}),
    ARMOUR       (new QueryStatType<Integer>(null, "ar",        QueryStatType.FilterSection.ARMOUR_FILTERS) {}),

    // weapon_filters — threshold 1
    ELEMENTAL_DPS(new QueryStatType<Integer>(null, "edps",      QueryStatType.FilterSection.WEAPON_FILTERS) {}),
    PHYSICAL_DPS (new QueryStatType<Integer>(null, "pdps",      QueryStatType.FilterSection.WEAPON_FILTERS) {}),
    LOCAL_CRIT   (new QueryStatType<Integer>(null, "crit",      QueryStatType.FilterSection.WEAPON_FILTERS) {}),

    // socket_filters — threshold 0 (include when value >= 0)
    LINKS        (new QueryStatType<Integer>(null, "links",     QueryStatType.FilterSection.SOCKET_FILTERS, 0) {}),

    // misc_filters — integers use threshold 0; boolean uses "option" wrapper
    ILVL         (new QueryStatType<Integer>(null, "ilvl",      QueryStatType.FilterSection.MISC_FILTERS, 0) {}),
    GEM_LEVEL    (new QueryStatType<Integer>(null, "gem_level", QueryStatType.FilterSection.MISC_FILTERS, 1) {}),
    CORRUPTED    (new QueryStatType<Boolean>(null, "corrupted", QueryStatType.FilterSection.MISC_FILTERS) {}),

    // type_filters — string written as a direct value
    RARITY       (new QueryStatType<String> (null, "rarity",    QueryStatType.FilterSection.TYPE_FILTERS) {});

    private final QueryStatType<?> type;

    QueryStat(QueryStatType<?> type) {
        this.type = type;
    }

    public QueryStatType<?> getType() {
        return type;
    }
}
