package poebuildexporter.code.poeapi;

import poebuildexporter.code.PropertiesManagerCore;

import java.util.Map;

public class PathOfExile2TradeApi extends PathOfExileTradeApi {

    @Override public String getLeague()        { return PropertiesManagerCore.getLeaguePoe2(); }
    @Override public String getStatsApiUrl()   { return PropertiesManagerCore.getStatsApiLinkPoe2(); }
    @Override public String getItemsApiUrl()   { return PropertiesManagerCore.getItemsApiLinkPoe2(); }
    @Override public String getTradeSearchUrl(){ return PropertiesManagerCore.getTradeApiSearchUrlPoe2(); }
    @Override public String getTradeWebUrl()   { return PropertiesManagerCore.getTradeWebSearchUrlPoe2(); }

    @Override
    protected Map<QueryStatType.FilterSection, QueryStatType.FilterSection> getFilterSectionRemaps() {
        return Map.of(
            QueryStatType.FilterSection.WEAPON_FILTERS, QueryStatType.FilterSection.EQUIPMENT_FILTERS,
            QueryStatType.FilterSection.ARMOUR_FILTERS,  QueryStatType.FilterSection.EQUIPMENT_FILTERS
        );
    }
}
