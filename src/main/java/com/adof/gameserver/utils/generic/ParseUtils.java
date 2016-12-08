package com.adof.gameserver.utils.generic;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by mukthar on 18/11/16.
 */
public class ParseUtils {

    public static JsonNode stringToJsonNode(String value) {
        JsonNode map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
            mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);

            // convert JSON string to Map
            map = mapper.readValue(value, JsonNode.class);
            //map = mapper.readValue(value, new TypeReference<Map<String, Object>>() {});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}
