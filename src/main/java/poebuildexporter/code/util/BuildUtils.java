package poebuildexporter.code.util;

import kotlin.collections.ArrayDeque;
import poebuildexporter.code.build.Build;
import poebuildexporter.code.build.BuildType;
import poebuildexporter.code.importer.BuildImporterResult;

import java.util.List;
import java.util.function.Consumer;

public class BuildUtils {

    public static List<Build> extractBuildsFromRawImportData(List<BuildImporterResult> importResults, Consumer<String> publishMessageCallback) {
        List<Build> results = new ArrayDeque<>();
        for (BuildImporterResult importResult : importResults) {
            publishMessageCallback.accept("Loading: " + importResult.rawBuildImportData());
            BuildType buildType = BuildType.deriveFromString(importResult.rawBuildImportData());
            if (buildType == null) {
                throw new IllegalArgumentException("Could not determine build type for: " + importResult.rawBuildImportData());
            }
            results.add(Build.of(buildType, importResult));
        }

        return results;
    }

    public static List<Build> extractBuildsFromRawImportData(List<BuildImporterResult> importResults) {
        return extractBuildsFromRawImportData(importResults, (s) -> {});
    }

}
