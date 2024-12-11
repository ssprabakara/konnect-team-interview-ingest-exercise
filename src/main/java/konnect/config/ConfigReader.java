package konnect.config;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import konnect.exception.ConfigNotFoundException;
import konnect.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE_PATH = "conf/settings.json";
    private static final String CONFIG_KEY_NOT_FOUND = "Config Key %s, is not found in the config.";
    private static final String CONFIG_VALUE_IS_NOT_AN_ARRAY = "The config value is not an array. Key: {}";
    private static final String CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE = "Config value is not of the " +
            "expected type - {}. Config Key: {}";
    private static final String NULL_CONFIG_KEY = "Config Key is null";
    private JsonNode jsonNode;

    public ConfigReader() {
        initializeConfig();
    }

    private void initializeConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
            jsonNode = Utils.OBJECT_MAPPER.readTree(is);
        } catch (final IOException ex) {
            LOGGER.error("Error occurred, while reading the JSON Config file.", ex);
        }
    }

    public List<String> getTextArrayValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            throw new ConfigNotFoundException(error);
        }

        if (!value.isArray()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_AN_ARRAY, propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Array.class, value));
        }

        List<String> values = new ArrayList<>();

        for (final JsonNode val: jsonNode.at(propKey)) {
            if (!val.isTextual()) {
                LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, String.class.getName(), propKey);
                throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, String.class, val));
            }
            values.add(val.asText());
        }
        return values;

    }

    public List<Integer> getIntArrayValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            throw new ConfigNotFoundException(error);
        }

        if (!value.isArray()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_AN_ARRAY, propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Array.class, value));
        }

        List<Integer> values = new ArrayList<>();

        for (final JsonNode val: jsonNode.at(propKey)) {
            if (!val.isInt()) {
                LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, Integer.class.getName(), propKey);
                throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Integer.class, val));
            }
            values.add(val.asInt());
        }
        return values;
    }

    public List<Long> getLongArrayValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            throw new ConfigNotFoundException(error);
        }

        if (!value.isArray()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_AN_ARRAY, propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Long.class, value));
        }

        List<Long> values = new ArrayList<>();
        for (final JsonNode val: jsonNode.at(propKey)) {
            if (!val.isLong()) {
                LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, Long.class.getName(), propKey);
                throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Long.class, val));
            }
            values.add(val.asLong());
        }
        return values;
    }

    public Boolean getBooleanValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            throw new ConfigNotFoundException(error);
        }

        if (!value.isBoolean()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, Boolean.class.getName(), propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Boolean.class, value));
        }

        return jsonNode.at(propKey).asBoolean();
    }

    public Integer getIntValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            throw new ConfigNotFoundException(error);
        }

        if (!value.isInt()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, Integer.class.getName(), propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Integer.class, value));
        }

        return jsonNode.at(propKey).asInt();
    }

    public Long getLongValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            throw new ConfigNotFoundException(error);
        }

        if (!value.isLong()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, Long.class.getName(), propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, Long.class, value));
        }

        return jsonNode.at(propKey).asLong();
    }

    public String getValue(final String key) {
        if (key == null) {
            LOGGER.error(NULL_CONFIG_KEY);
            throw new ConfigNotFoundException(NULL_CONFIG_KEY);
        }

        String propKey = buildKey(key);
        JsonNode value = jsonNode.at(propKey);

        if (value == null) {
            String error = String.format(CONFIG_KEY_NOT_FOUND, propKey);
            LOGGER.error(error);
            return null;
        }

        if (!value.isTextual()) {
            LOGGER.error(CONFIG_VALUE_IS_NOT_OF_THE_EXPECTED_TYPE, String.class.getName(), propKey);
            throw new ConfigNotFoundException(buildTypeMismatchErrorMessage(propKey, String.class, value));
        }

        return jsonNode.at(propKey).asText();
    }

    private String buildKey(final String key) {
       return (key == null) ? null : "/" + key.replace(".", "/");
    }

    private String buildTypeMismatchErrorMessage(final String key, final Class<?> expectedType, final Object value) {
        return "Type mismatch for key: " + key +
                ". Expected: " + expectedType + " Got: " +
                (value != null ? value.getClass() : null);
    }
}
