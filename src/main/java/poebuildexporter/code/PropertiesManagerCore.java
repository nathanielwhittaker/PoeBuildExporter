package poebuildexporter.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesManagerCore {

    private static final Logger log = LoggerFactory.getLogger(PropertiesManagerCore.class);

    public static final String PROPERTIES_DIR = "properties/";
    private static final Properties properties = new Properties();
    private static final Properties itemParsingProperties = new Properties();
    private static final Properties runeStatsProperties = new Properties();
    private static final Properties baseUserConfigProperties = new Properties();
    private static Set<String> uniqueItemWhitelist = Set.of();
    static {
        reloadProperties();
    }

    public static void reloadProperties() {
        loadFile(properties,               "PathOfExileApiQuery.properties");
        loadFile(itemParsingProperties,    "ItemParsing.properties");
        loadFile(runeStatsProperties,      "RuneStats.properties");
        ensureBaseUserConfig();
        loadFile(baseUserConfigProperties, "BaseUserConfig.properties");
        uniqueItemWhitelist = loadUniqueItemWhitelist();
    }

    private static void ensureBaseUserConfig() {
        Path target = Paths.get(PROPERTIES_DIR + "BaseUserConfig.properties");
        if (!Files.exists(target)) {
            Path example = Paths.get(PROPERTIES_DIR + "BaseUserConfig.properties.example");
            try {
                Files.copy(example, target);
                log.info("Created BaseUserConfig.properties from example — fill in your POESESSID and league settings.");
            } catch (IOException e) {
                log.error("Failed to create BaseUserConfig.properties from example", e);
                throw new RuntimeException(e);
            }
        }
    }

    private static Set<String> loadUniqueItemWhitelist() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PROPERTIES_DIR + "UniqueItems.txt"))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IOException e) {
            log.error("Failed to read UniqueItems.txt", e);
            return Set.of();
        }
    }

    public static void setProperty(String filename, String key, String value) {
        Path path = Paths.get(PROPERTIES_DIR + filename);
        try {
            List<String> lines = Files.readAllLines(path);
            boolean found = false;
            List<String> updated = new java.util.ArrayList<>();
            for (String line : lines) {
                if (line.startsWith(key + "=") || line.startsWith(key + " =")) {
                    updated.add(key + "=" + value);
                    found = true;
                } else {
                    updated.add(line);
                }
            }
            if (!found) {
                updated.add(key + "=" + value);
            }
            Files.write(path, updated);
            reloadProperties();
        } catch (IOException e) {
            log.error("Failed to write property '{}' to {}", key, filename, e);
        }
    }

    private static void loadFile(Properties target, String filename) {
        try (FileInputStream fis = new FileInputStream(PROPERTIES_DIR + filename)) {
            target.load(fis);
        } catch (FileNotFoundException e) {
            log.error("Properties file not found: {}", filename, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Failed to read properties file: {}", filename, e);
            throw new RuntimeException(e);
        }
    }

    public static String getPoeSessId() {
        return baseUserConfigProperties.getProperty("POESESSID");
    }

    public static void setPoeSessId(String value) {
        setProperty("BaseUserConfig.properties", "POESESSID", value);
    }

    public static String getStatsApiLink() {
        return properties.getProperty("statsApiLink");
    }

    public static String getStatsApiLinkPoe2() {
        return properties.getProperty("statsApiLinkPoE2");
    }

    public static String getItemsApiLink() {
        return properties.getProperty("itemsApiLink");
    }

    public static String getItemsApiLinkPoe2() {
        return properties.getProperty("itemsApiLinkPoE2");
    }

    public static String getLeague() {
        return baseUserConfigProperties.getProperty("league");
    }

    public static void setLeague(String value) {
        setProperty("BaseUserConfig.properties", "league", value);
    }

    public static String getLeaguePoe2() {
        return baseUserConfigProperties.getProperty("leaguePoE2");
    }

    public static void setLeaguePoe2(String value) {
        setProperty("BaseUserConfig.properties", "leaguePoE2", value);
    }

    public static String getTradeApiSearchUrl() {
        return properties.getProperty("tradeApiSearchUrl");
    }

    public static String getTradeApiSearchUrlPoe2() {
        return properties.getProperty("tradeApiSearchUrlPoE2");
    }

    public static long getTradeApiRequestDelayMs() {
        return Long.parseLong(properties.getProperty("tradeApiRequestDelayMs", "3000"));
    }

    public static String getTradeWebSearchUrl() {
        return properties.getProperty("tradeWebSearchUrl");
    }

    public static String getTradeWebSearchUrlPoe2() {
        return properties.getProperty("tradeWebSearchUrlPoE2");
    }

    public static double getStatRollDelta() {
        return Double.parseDouble(itemParsingProperties.getProperty("statRollDelta", "1.0"));
    }

    public static List<String> getRollDeltaExclusions() {
        String raw = itemParsingProperties.getProperty("rollDeltaExclusions", "");
        if (raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static Set<String> getUniqueItemWhitelist() {
        return uniqueItemWhitelist;
    }

    public static Map<String, List<String>> getRuneStatsMap() {
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (String key : runeStatsProperties.stringPropertyNames()) {
            String raw = runeStatsProperties.getProperty(key, "");
            if (raw.isBlank()) continue;
            List<String> stats = Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            map.put(key.trim().toLowerCase(), stats);
        }
        return map;
    }
}
