package poebuildexporter.code.build;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.poeapi.PathOfExileTradeApi;
import poebuildexporter.code.util.Web;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.InflaterInputStream;

public abstract class Pobbin extends Build {

    private static final Logger log = LoggerFactory.getLogger(Pobbin.class);

    //Web data
    private String url;
    private String pobXml;
    private String html;

    //Build data
    private String name;
    private String buildData;
    private List<String> itemDataList;
    protected Pobbin(String url, String name) {
        this.url = url;
        this.name = name;
        log.info("Loading build '{}' from {}", name, url);
        this.html = new Web(url).getResponse();
        parsePobbinPobXml();
        parsePobXmlIntoBuildData();
        parseBuildDataIntoItemData();
        this.items = itemDataList.stream()
                .filter(s -> s.startsWith("Rarity:"))
                .map(s -> Item.fromStringForBuild(s, this))
                .toList();
        log.info("Loaded build '{}' with {} items", name, this.items.size());
    }

    public static String parseNameFromHtml(String html) {
        return getSubstringBasedOnPrefixAndSuffix(
                html,
                "property=\"og:title\" content=\"",
                "\"/><meta data-xx=\"1.6\""
        );
    }

    //TODO move to utils class or get some other lib.
    private static String getSubstringBasedOnPrefixAndSuffix(String base, String prefix, String suffix) {
        if(StringUtils.isNoneBlank(base, prefix, suffix)) {
            int startIndex = base.indexOf(prefix) + prefix.length();
            int endIndex = base.indexOf(suffix, startIndex);
            return base.substring(startIndex, endIndex);
        } else {
            throw new NullPointerException("Cannot parse blank.");
        }
    }

    private static String cleanItemData(String itemData) {
        itemData = itemData.replaceAll(
                "<ModRange\\s+range=\"[^\"]*\"\\s+id=\"[^\"]*\"\\s*/>",
                ""
        );
        itemData = itemData.replaceAll("<Item\\s+id=\"[^\"]*\"[^>]*>", "");
        itemData = itemData.replaceAll("\\s{2,}", "\n\n");
        int endIndex = itemData.indexOf("</Items");
        itemData = itemData.substring(0, endIndex + "</Items>".length());
        return itemData;
    }

    public Pobbin parsePobbinPobXml() {
        if(StringUtils.isNotBlank(html)) {
            this.pobXml = getSubstringBasedOnPrefixAndSuffix(
                    html,
                    "aria-label=\"Path of Building buildcode\" readonly=\"\">",
                    "</textarea>"
            );
            return this;
        } else {
            throw new NullPointerException("Cannot parse null.");
        }
    }

    public Pobbin parsePobXmlIntoBuildData() {
        if(StringUtils.isNotBlank(pobXml)) {
            byte[] bytes = Base64.getUrlDecoder().decode(pobXml);
            try (InflaterInputStream gis = new InflaterInputStream(new ByteArrayInputStream(bytes))) {
                this.buildData = new String(gis.readAllBytes(), StandardCharsets.UTF_8);
                return this;
            } catch (Exception e) {
                log.error("Failed to decompress PobXML build data", e);
            }
        } else {
            throw new NullPointerException("Cannot parse null.");
        }
        return null;
    }

    //Fixme clean me
    public Pobbin parseBuildDataIntoItemData() {
        String tmpItemData = StringUtils.replaceAll(buildData, "<Items\\s+[^>]*>", "<Items>");
        tmpItemData = StringEscapeUtils.unescapeXml(cleanItemData(tmpItemData));
        String itemDataString = getSubstringBasedOnPrefixAndSuffix(tmpItemData, "<Items>", "</Items>");

        List<String> itemDataList = Arrays.stream(itemDataString.split("</Item>"))
                .map(str -> {
                    str = str.trim();
                    str = str.replaceAll("\\{[^}]*\\}", "");
                    return str;
                })
                .toList();
        this.itemDataList = itemDataList;

        return this;
    }

public String getPobXml() {
        if(null == pobXml) {
            parsePobbinPobXml();
        }
        return pobXml;
    }

    public String html() {
        return html;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getItemDataList() {
        return itemDataList;
    }

    public String getBuildData() {
        return buildData;
    }

}
