package poebuildexporter.code.poeapi;

import poebuildexporter.code.ItemData.Stat;

public abstract class QueryStatType<T> {

    public enum FilterSection {
        TYPE_FILTERS("type_filters"),
        SOCKET_FILTERS("socket_filters"),
        MISC_FILTERS("misc_filters"),
        WEAPON_FILTERS("weapon_filters"),
        ARMOUR_FILTERS("armour_filters"),
        EQUIPMENT_FILTERS("equipment_filters");

        private final String jsonKey;

        FilterSection(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String getJsonKey() {
            return jsonKey;
        }
    }

    private final Stat stat;
    private final String tradeApiStatLabel;
    private final FilterSection filterSection;
    private final int minThreshold;
    private T val;

    public QueryStatType(Stat stat, String tradeApiStatLabel, FilterSection filterSection) {
        this(stat, tradeApiStatLabel, filterSection, 1);
    }

    public QueryStatType(Stat stat, String tradeApiStatLabel, FilterSection filterSection, int minThreshold) {
        this.stat = stat;
        this.tradeApiStatLabel = tradeApiStatLabel;
        this.filterSection = filterSection;
        this.minThreshold = minThreshold;
    }

    public Stat getStat() {
        return stat;
    }

    public String getTradeApiStatLabel() {
        return tradeApiStatLabel;
    }

    public FilterSection getFilterSection() {
        return filterSection;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setVal(T val) {
        this.val = val;
    }

    public T getVal() {
        return val;
    }
}
