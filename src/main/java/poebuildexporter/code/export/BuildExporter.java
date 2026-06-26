package poebuildexporter.code.export;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.build.Build;
import poebuildexporter.code.poeapi.PathOfExileTradeApiResponse;

import java.util.Map;

public interface BuildExporter {

    String getDisplayName();

    ExportType getExportType();

    void export(Map<Build, Map<Item, PathOfExileTradeApiResponse>> results);
}
