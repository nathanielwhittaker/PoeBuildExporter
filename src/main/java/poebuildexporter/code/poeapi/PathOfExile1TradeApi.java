package poebuildexporter.code.poeapi;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.PropertiesManagerCore;

import java.util.LinkedHashMap;
import java.util.Map;

public class PathOfExile1TradeApi extends PathOfExileTradeApi {

    @Override public String getLeague()        { return PropertiesManagerCore.getLeague(); }
    @Override public String getStatsApiUrl()   { return PropertiesManagerCore.getStatsApiLink(); }
    @Override public String getItemsApiUrl()   { return PropertiesManagerCore.getItemsApiLink(); }
    @Override public String getTradeSearchUrl(){ return PropertiesManagerCore.getTradeApiSearchUrl(); }
    @Override public String getTradeWebUrl()   { return PropertiesManagerCore.getTradeWebSearchUrl(); }

    @Override
    protected Map<QueryStat, Object> versionSpecificFilters(Item item) {
        Map<QueryStat, Object> filters = new LinkedHashMap<>();
        if (item.getLinks() > 0) filters.put(QueryStat.LINKS, item.getLinks());
        return filters;
    }
}
