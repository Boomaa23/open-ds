package com.boomaa.opends.mcp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Lightweight JSON array representation for building MCP responses.
 */
public class JsonList implements Iterable<Object> {
    private final List<Object> items = new ArrayList<>();

    public void add(Object value) {
        items.add(value);
    }

    public Object get(int index) {
        return items.get(index);
    }

    public int size() {
        return items.size();
    }

    @Override
    public Iterator<Object> iterator() {
        return items.iterator();
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Object item : items) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(JsonUtil.valueToJson(item));
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
