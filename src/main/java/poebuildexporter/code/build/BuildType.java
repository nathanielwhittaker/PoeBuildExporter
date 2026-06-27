package poebuildexporter.code.build;

import poebuildexporter.code.importer.BuildImporterResult;
import poebuildexporter.code.util.Web;

public enum BuildType {
    POBBIN {
        @Override
        public Build create(BuildImporterResult importResult) {
            String url = importResult.rawBuildImportData();
            String html = new Web(url).getResponse();
            String pobbinName = Pobbin.parseNameFromHtml(html);
            String name = importResult.name() != null ? importResult.name() : pobbinName;
            if (pobbinName.trim().endsWith("[PoE 2]")) {
                return new Poe2Pobbin(url, name);
            }
            return new Poe1Pobbin(url, name);
        }

        @Override
        public boolean deriveCondition(String rawImportData) {
            return rawImportData.contains("pobb.in");
        }
    };

    public abstract Build create(BuildImporterResult importResult);
    public abstract boolean deriveCondition(String rawImportData);

    /**
     *
     * @param rawBuildImportData the raw build import data given by the user. Typically a pobb.in link.
     * @return the build type that best represents this string
     */
    public static BuildType deriveFromString(String rawBuildImportData) {
        for(BuildType buildType : BuildType.values()) {
            if(buildType.deriveCondition(rawBuildImportData)) {
                return buildType;
            }
        }
        return null;
    }
}
