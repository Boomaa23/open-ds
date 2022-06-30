package com.boomaa.opends.util;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class NumberUtils {
    public static double getDouble(byte[] bytes) throws BufferUnderflowException {
        return byteWrapBig(bytes).getDouble();
    }

    public static float getFloat(byte[] bytes) {
        return byteWrapBig(bytes).getFloat();
    }

    public static int getInt32(byte[] bytes) {
        return byteWrapBig(bytes).getInt();
    }

    private static ByteBuffer byteWrapBig(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
    }

    public static int getUInt32(byte[] bytes) {
        return (bytes[3] & 0xFF)
                | ((bytes[2] & 0xFF) << 8)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[0] & 0xFF) << 24);
    }

    public static int getUInt16(byte[] nums) {
        return ((nums[0] & 0xFF) << 8) | (nums[1] & 0xFF);
    }

    public static int getUInt10(int[] nums) {
        int out = 0;
        for (int i = 0; i < nums.length; i++) {
            out += Math.pow(2, (nums.length - i - 1)) * nums[i];
        }
        return out;
    }

    public static int getUInt8(byte num) {
        return num < 0 ? 256 + num : num;
    }

    public static byte[] intToBytePair(int in) {
        byte[] out = new byte[2];
        out[0] = (byte) ((in >>> 8) & 0xFF);
        out[1] = (byte) (in & 0xFF);
        return out;
    }

    public static byte[] intToByteQuad(int in) {
        return ByteBuffer.allocate(4).putInt(in).array();
    }

    public static byte[] longToByteOctet(long in) {
        return ByteBuffer.allocate(8).putLong(in).array();
    }

    public static int dblToInt8(double in) {
        // range [-128, 127] for joysticks
        return (int) (in * (in < 0 ? 128 : 127));
    }

    // Decode ULEB128 encoded data (byte array)
    // Index 0 must be start of ULEB128 size tag
    public static int decodeULEB128(byte[] data) {
        int value = 0;
        int bytesRead = 0;
        boolean continueReading;
        do {
            final byte rawByteValue = data[bytesRead];
            if (bytesRead == 9 && (rawByteValue & ~0x1) != 0) {
                throw new IllegalStateException("ULEB128 sequence exceeds 64bits");
            }

            value |= (rawByteValue & 0x7FL) << (bytesRead * 7);

            bytesRead++;
            continueReading = ((rawByteValue & 0x80) != 0);
        } while (continueReading);

        return value;
    }

    // ULEB128 encoding for a string value
    public static List<Byte> encodeULEB128(String value) {
        List<Byte> bytes = encodeULEB128(value.length());
        for (byte b : value.getBytes()) {
            bytes.add(b);
        }
        return bytes;
    }

    // ULEB128 encoding for data of length len
    public static List<Byte> encodeULEB128(long len) {
        List<Byte> bytes = new ArrayList<>();
        do {
            byte b = (byte) (len & 0x7F);
            len >>= 7;
            if (len != 0) {
                b |= 0x80;
            }
            bytes.add(b);
        } while (len != 0);
        return bytes;
    }

    // Number of bytes needed to represent data of length len
    public static int sizeULEB128(int len) {
        int groupCount = 0;
        do {
            groupCount++;
            len >>>= 7;
        } while (len != 0);
        return groupCount;
    }

    public static boolean hasMaskMatch(byte b1, int i2) {
        return (b1 & i2) == i2;
    }

    public static boolean hasPlacedBit(byte b, int bitmaskPos) {
        return padByte(b).toCharArray()[bitmaskPos] == 1;
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

    public static double roundTo(double value, int decimals) {
        double pow = Math.pow(10, decimals);
        return Math.round(value * pow) / pow;
    }

    public static double limit(double value, double min, double max) {
        return value > max ? max : Math.max(value, min);
    }

    public static byte[] packBools(boolean[] bools) {
        int numBools = bools.length;
        int packedSize = numBools >> 3;
        if ((numBools & 0x07) != 0) {
            packedSize++;
        }
        byte[] out = new byte[packedSize];
        for (int i = 0; i < bools.length; i++) {
            if (bools[i]) {
                out[i >> 3] |= (byte) 1 << (i & 0x07);
            }
        }
        // Output bytes are reversed
        byte[] flipped = new byte[out.length];
        for (int i = 0; i < flipped.length; i++) {
            flipped[i] = out[flipped.length - i - 1];
        }
        return flipped;
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
                    n = getUInt16(new byte[] { bytes[i], bytes[++i]});
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
