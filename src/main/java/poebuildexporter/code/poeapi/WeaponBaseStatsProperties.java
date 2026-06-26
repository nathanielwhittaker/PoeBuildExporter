package poebuildexporter.code.poeapi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import poebuildexporter.code.PropertiesManagerCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeaponBaseStatsProperties {

    private static final Logger log = LoggerFactory.getLogger(WeaponBaseStatsProperties.class);

    private final Map<String, WeaponBaseStats> stats = new LinkedHashMap<>();

    public WeaponBaseStatsProperties(String fileName) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PropertiesManagerCore.PROPERTIES_DIR + fileName)) {
            props.load(fis);
        } catch (IOException e) {
            log.error("Failed to load weapon base stats from {}", fileName, e);
            throw new RuntimeException(e);
        }
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key, "").trim();
            if (value.isEmpty()) continue;
            String[] parts = value.split(",");
            if (parts.length < 3) continue;
            try {
                int physMin = Integer.parseInt(parts[0].trim());
                int physMax = Integer.parseInt(parts[1].trim());
                double aps  = Double.parseDouble(parts[2].trim());
                stats.put(key.trim(), new WeaponBaseStats(physMin, physMax, aps));
            } catch (NumberFormatException ignored) {}
        }
    }

    public WeaponBaseStats getStats(String baseType) {
        if (baseType == null) return null;
        return stats.get(baseType);
    }
}
