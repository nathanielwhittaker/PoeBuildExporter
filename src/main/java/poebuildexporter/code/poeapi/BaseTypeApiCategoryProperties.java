package poebuildexporter.code.poeapi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import poebuildexporter.code.PropertiesManagerCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTypeApiCategoryProperties {

    private static final Logger log = LoggerFactory.getLogger(BaseTypeApiCategoryProperties.class);

    private final Map<String, String> categories = new LinkedHashMap<>();

    public BaseTypeApiCategoryProperties(String fileName) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PropertiesManagerCore.PROPERTIES_DIR + fileName)) {
            props.load(fis);
        } catch (IOException e) {
            log.error("Failed to load base type categories from {}", fileName, e);
            throw new RuntimeException(e);
        }
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key, "").trim();
            if (!key.isEmpty() && !value.isEmpty()) {
                categories.put(key, value);
            }
        }
    }

    public String getCategory(String baseType) {
        return categories.get(baseType);
    }
}
