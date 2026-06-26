package poebuildexporter.code.build;

import java.util.List;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.PropertiesManagerCore;
import poebuildexporter.code.poeapi.BaseTypeApiCategoryProperties;
import poebuildexporter.code.poeapi.PathOfExile1TradeApi;
import poebuildexporter.code.poeapi.PathOfExile2TradeApi;
import poebuildexporter.code.poeapi.PathOfExileTradeApi;
import poebuildexporter.code.poeapi.WeaponBaseStatsProperties;

public enum PoeVersion implements PoeGameVersionConfig {

    POE1 {
        private final BaseTypeApiCategoryProperties categories =
                new BaseTypeApiCategoryProperties("PathOfExile1BaseTypeApiCategory.properties");
        private final WeaponBaseStatsProperties weaponStats =
                new WeaponBaseStatsProperties("PathOfExile1WeaponBaseStatsProperties.properties");
        private final PathOfExileTradeApi tradeApi = new PathOfExile1TradeApi();

        @Override public String getLeague()       { return PropertiesManagerCore.getLeague(); }
        @Override public String getStatsApiUrl()  { return PropertiesManagerCore.getStatsApiLink(); }
        @Override public String getItemsApiUrl()  { return PropertiesManagerCore.getItemsApiLink(); }
        @Override public BaseTypeApiCategoryProperties getBaseTypeCategoryProperties() { return categories; }
        @Override public WeaponBaseStatsProperties getWeaponBaseStats() { return weaponStats; }
        @Override public PathOfExileTradeApi getTradeApi()              { return tradeApi; }
        @Override public List<Stat> getTradeStats()                     { return tradeApi.getStats(); }
        @Override public List<Item> getTradeBaseTypesWithLocalMods()    { return tradeApi.getBaseTypesWithLocalMods(); }
        @Override public List<Item> getAllBaseTypes()                   { return tradeApi.getAllBaseTypes(); }
    },

    POE2 {
        private final BaseTypeApiCategoryProperties categories =
                new BaseTypeApiCategoryProperties("PathOfExile2BaseTypeApiCategory.properties");
        private final WeaponBaseStatsProperties weaponStats =
                new WeaponBaseStatsProperties("PathOfExile2WeaponBaseStatsProperties.properties");
        private final PathOfExileTradeApi tradeApi = new PathOfExile2TradeApi();

        @Override public String getLeague()       { return PropertiesManagerCore.getLeaguePoe2(); }
        @Override public String getStatsApiUrl()  { return PropertiesManagerCore.getStatsApiLinkPoe2(); }
        @Override public String getItemsApiUrl()  { return PropertiesManagerCore.getItemsApiLinkPoe2(); }
        @Override public BaseTypeApiCategoryProperties getBaseTypeCategoryProperties() { return categories; }
        @Override public WeaponBaseStatsProperties getWeaponBaseStats() { return weaponStats; }
        @Override public PathOfExileTradeApi getTradeApi()              { return tradeApi; }
        @Override public List<Stat> getTradeStats()                     { return tradeApi.getStats(); }
        @Override public List<Item> getTradeBaseTypesWithLocalMods()    { return tradeApi.getBaseTypesWithLocalMods(); }
        @Override public List<Item> getAllBaseTypes()                   { return tradeApi.getAllBaseTypes(); }
    };
}
