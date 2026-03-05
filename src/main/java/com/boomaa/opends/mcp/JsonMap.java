package com.boomaa.opends.mcp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lightweight JSON object representation for building MCP responses.
 */
public class JsonMap implements Iterable<Map.Entry<String, Object>> {
    private final LinkedHashMap<String, Object> entries = new LinkedHashMap<>();

    public void put(String key, Object value) {
        entries.put(key, value);
    }

    public Object get(String key) {
        return entries.get(key);
    }

    public String getString(String key) {
        Object val = entries.get(key);
        return val instanceof String ? (String) val : null;
    }

    public JsonMap getMap(String key) {
        Object val = entries.get(key);
        return val instanceof JsonMap ? (JsonMap) val : null;
    }

    public boolean containsKey(String key) {
        return entries.containsKey(key);
    }

    public int size() {
        return entries.size();
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return entries.entrySet().iterator();
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"').append(JsonUtil.escapeString(entry.getKey())).append('"');
            sb.append(':');
            sb.append(JsonUtil.valueToJson(entry.getValue()));
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
