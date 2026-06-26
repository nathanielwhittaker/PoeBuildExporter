package poebuildexporter.code.util;

import kotlin.collections.ArrayDeque;
import poebuildexporter.code.build.Build;
import poebuildexporter.code.build.BuildType;

import java.util.List;
import java.util.function.Consumer;

public class BuildUtils {

    public static List<Build> extractBuildsFromRawImportData(List<String> rawImportDataList, Consumer<String> publishMessageCallback) {
        List<Build> results = new ArrayDeque<>();
        for (String importedBuildString : rawImportDataList) {
            publishMessageCallback.accept("Loading: " + importedBuildString);
            BuildType buildType = BuildType.deriveFromString(importedBuildString);
            if (buildType == null) {
                throw new IllegalArgumentException("Could not determine build type for: " + importedBuildString);
            }
            results.add(Build.of(buildType, importedBuildString));
        }

        return results;
    }

    public static List<Build> extractBuildsFromRawImportData(List<String> rawImportDataList) {
        return extractBuildsFromRawImportData(rawImportDataList, (s) -> {});
    }

}
