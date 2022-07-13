package com.boomaa.opends.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    private StringUtils() {
    }

    public static String padByte(byte b) {
        return padByte(b, 8);
    }

    public static String padByte(byte b, int size) {
        String formatStr = "%" + size + "s";
        return String.format(formatStr, Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static String padDouble(double value, int decimals) {
        String strVal = String.valueOf(value);
        StringBuilder sb = new StringBuilder(strVal);
        int indPDec = strVal.indexOf('.');
        if (indPDec != -1) {
            for (int i = sb.length() - indPDec - 1; i < decimals; i++) {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    public static String padInt(int value, int digits) {
        String strVal = String.valueOf(value);
        StringBuilder sb = new StringBuilder();
        int len = digits - strVal.length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                sb.append('0');
            }
        }
        sb.append(strVal);
        return sb.toString();
    }

    public static String bytesHumanReadable(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static String toTitleCase(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i == 0 || text.charAt(i - 1) == ' ') {
                sb.append(Character.toUpperCase(text.charAt(i)));
            } else {
                sb.append(Character.toLowerCase(text.charAt(i)));
            }
        }
        return sb.toString();
    }

    public static String[] extractAllASCII(byte[] bytes) {
        List<String> asciiList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            char val = (char) bytes[i];
            if (val > 31 && val < 127) {
                sb.append(val);
            } else if (sb.length() > 0) {
                asciiList.add(sb.toString());
                sb = new StringBuilder();
            }
            if (i == bytes.length - 1 && sb.length() > 0) {
                asciiList.add(sb.toString());
            }
        }
        return asciiList.toArray(new String[0]);
    }

    public static String[] getNLengthStrs(byte[] bytes, int nSize, boolean strictASCII) {
        if (nSize < 1 || nSize > 2) {
            throw new IllegalArgumentException("Not a valid n-size: \"" + nSize + "\"");
        }
        List<String> asciiList = new ArrayList<>();
        int i = 0;
        int n = -1;
        while (i < bytes.length) {
            if (n == -1) {
                if (nSize == 1) {
                    n = bytes[i];
                } else if (i + 1 < bytes.length) {
                    n = NumberUtils.getUInt16(new byte[] { bytes[i], bytes[++i]});
                }
                i++;
            } else {
                String rawValue = new String(ArrayUtils.slice(bytes, i, i + n));
                if (strictASCII) {
                    StringBuilder sb = new StringBuilder();
                    char[] valChar = rawValue.toCharArray();
                    for (char currChar : valChar) {
                        if (currChar > 31 && currChar < 127) {
                            sb.append(currChar);
                        }
                    }
                    asciiList.add(sb.toString());
                } else {
                    asciiList.add(rawValue);
                }
                i += n;
                n = -1;
            }
        }
        String[] out = asciiList.toArray(new String[0]);
        return strictASCII ? ArrayUtils.removeBlanks(out) : out;
    }
}
