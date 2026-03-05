package com.boomaa.opends.mcp;

/**
 * Minimal JSON parser for MCP JSON-RPC requests.
 * Handles the subset of JSON needed for MCP protocol messages.
 */
public final class JsonParser {
    private final String json;
    private int pos;

    private JsonParser(String json) {
        this.json = json;
        this.pos = 0;
    }

    public static JsonMap parseObject(String json) {
        return new JsonParser(json.trim()).readObject();
    }

    private JsonMap readObject() {
        JsonMap map = new JsonMap();
        expect('{');
        skipWhitespace();
        if (peek() == '}') {
            advance();
            return map;
        }
        while (true) {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            Object value = readValue();
            map.put(key, value);
            skipWhitespace();
            if (peek() == ',') {
                advance();
            } else {
                break;
            }
        }
        expect('}');
        return map;
    }

    private JsonList readArray() {
        JsonList list = new JsonList();
        expect('[');
        skipWhitespace();
        if (peek() == ']') {
            advance();
            return list;
        }
        while (true) {
            skipWhitespace();
            list.add(readValue());
            skipWhitespace();
            if (peek() == ',') {
                advance();
            } else {
                break;
            }
        }
        expect(']');
        return list;
    }

    private Object readValue() {
        skipWhitespace();
        char c = peek();
        if (c == '"') {
            return readString();
        } else if (c == '{') {
            return readObject();
        } else if (c == '[') {
            return readArray();
        } else if (c == 't') {
            expectLiteral("true");
            return Boolean.TRUE;
        } else if (c == 'f') {
            expectLiteral("false");
            return Boolean.FALSE;
        } else if (c == 'n') {
            expectLiteral("null");
            return null;
        } else if (c == '-' || Character.isDigit(c)) {
            return readNumber();
        } else {
            throw new IllegalArgumentException("Unexpected character at position " + pos + ": " + c);
        }
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < json.length()) {
            char c = json.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            } else if (c == '\\') {
                if (pos >= json.length()) {
                    throw new IllegalArgumentException("Unexpected end of string escape");
                }
                char escaped = json.charAt(pos++);
                switch (escaped) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(escaped);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        String hex = json.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    default:
                        sb.append(escaped);
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Unterminated string");
    }

    private Number readNumber() {
        int start = pos;
        if (peek() == '-') {
            advance();
        }
        while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
            advance();
        }
        boolean isFloat = false;
        if (pos < json.length() && json.charAt(pos) == '.') {
            isFloat = true;
            advance();
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                advance();
            }
        }
        if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
            isFloat = true;
            advance();
            if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) {
                advance();
            }
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                advance();
            }
        }
        String numStr = json.substring(start, pos);
        if (isFloat) {
            return Double.parseDouble(numStr);
        } else {
            long val = Long.parseLong(numStr);
            if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
                return (int) val;
            }
            return val;
        }
    }

    private void expect(char expected) {
        skipWhitespace();
        if (pos >= json.length() || json.charAt(pos) != expected) {
            throw new IllegalArgumentException(
                "Expected '" + expected + "' at position " + pos
                    + " but got '" + (pos < json.length() ? json.charAt(pos) : "EOF") + "'");
        }
        pos++;
    }

    private void expectLiteral(String literal) {
        for (int i = 0; i < literal.length(); i++) {
            if (pos >= json.length() || json.charAt(pos) != literal.charAt(i)) {
                throw new IllegalArgumentException("Expected '" + literal + "' at position " + pos);
            }
            pos++;
        }
    }

    private char peek() {
        if (pos >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON at position " + pos);
        }
        return json.charAt(pos);
    }

    private void advance() {
        pos++;
    }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }
}
