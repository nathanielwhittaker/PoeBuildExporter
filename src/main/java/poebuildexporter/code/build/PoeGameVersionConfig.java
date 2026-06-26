package poebuildexporter.code.build;

import java.util.List;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.poeapi.BaseTypeApiCategoryProperties;
import poebuildexporter.code.poeapi.PathOfExileTradeApi;
import poebuildexporter.code.poeapi.WeaponBaseStatsProperties;

public interface PoeGameVersionConfig {
    String getLeague();
    String getStatsApiUrl();
    String getItemsApiUrl();
    BaseTypeApiCategoryProperties getBaseTypeCategoryProperties();
    WeaponBaseStatsProperties getWeaponBaseStats();
    PathOfExileTradeApi getTradeApi();
    List<Stat> getTradeStats();
    List<Item> getTradeBaseTypesWithLocalMods();
    List<Item> getAllBaseTypes();
}
