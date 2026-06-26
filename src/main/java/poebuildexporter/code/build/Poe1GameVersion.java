package poebuildexporter.code.build;

import java.util.List;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.poeapi.BaseTypeApiCategoryProperties;
import poebuildexporter.code.poeapi.PathOfExileTradeApi;
import poebuildexporter.code.poeapi.WeaponBaseStatsProperties;

public interface Poe1GameVersion extends PoeGameVersionConfig {

    @Override default String getLeague()      { return PoeVersion.POE1.getLeague(); }
    @Override default String getStatsApiUrl() { return PoeVersion.POE1.getStatsApiUrl(); }
    @Override default String getItemsApiUrl() { return PoeVersion.POE1.getItemsApiUrl(); }
    @Override default BaseTypeApiCategoryProperties getBaseTypeCategoryProperties() { return PoeVersion.POE1.getBaseTypeCategoryProperties(); }
    @Override default WeaponBaseStatsProperties getWeaponBaseStats() { return PoeVersion.POE1.getWeaponBaseStats(); }
    @Override default PathOfExileTradeApi getTradeApi() { return PoeVersion.POE1.getTradeApi(); }
    @Override default List<Stat> getTradeStats()     { return PoeVersion.POE1.getTradeStats(); }
    @Override default List<Item> getTradeBaseTypesWithLocalMods() { return PoeVersion.POE1.getTradeBaseTypesWithLocalMods(); }
    @Override default List<Item> getAllBaseTypes()     { return PoeVersion.POE1.getAllBaseTypes(); }
}
