package poebuildexporter.code.build;

import java.util.List;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.poeapi.BaseTypeApiCategoryProperties;
import poebuildexporter.code.poeapi.PathOfExileTradeApi;
import poebuildexporter.code.poeapi.WeaponBaseStatsProperties;

public interface Poe2GameVersion extends PoeGameVersionConfig {

    @Override default String getLeague()      { return PoeVersion.POE2.getLeague(); }
    @Override default String getStatsApiUrl() { return PoeVersion.POE2.getStatsApiUrl(); }
    @Override default String getItemsApiUrl() { return PoeVersion.POE2.getItemsApiUrl(); }
    @Override default BaseTypeApiCategoryProperties getBaseTypeCategoryProperties() { return PoeVersion.POE2.getBaseTypeCategoryProperties(); }
    @Override default WeaponBaseStatsProperties getWeaponBaseStats() { return PoeVersion.POE2.getWeaponBaseStats(); }
    @Override default PathOfExileTradeApi getTradeApi() { return PoeVersion.POE2.getTradeApi(); }
    @Override default List<Stat> getTradeStats()     { return PoeVersion.POE2.getTradeStats(); }
    @Override default List<Item> getTradeBaseTypesWithLocalMods() { return PoeVersion.POE2.getTradeBaseTypesWithLocalMods(); }
    @Override default List<Item> getAllBaseTypes()     { return PoeVersion.POE2.getAllBaseTypes(); }
}
