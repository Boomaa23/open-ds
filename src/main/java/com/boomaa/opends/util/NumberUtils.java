package com.boomaa.opends.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class NumberUtils {
    public static double getDouble(byte[] bytes) {
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

    public static byte[] intToBytePair(int in) {
        byte[] out = new byte[2];
        out[0] = (byte) ((in >>> 8) & 0xFF);
        out[1] = (byte) (in & 0xFF);
        return out;
    }

    public static byte[] intToByteQuad(int in) {
        return ByteBuffer.allocate(4).putInt(in).array();
    }

    public static int dblToInt8(double in) {
        return (int) (in * 127);
    }

    //TODO test to make sure this works
    public static int readULEB128(byte[] data) {
        int result = 0;
        int shift = 0;
        int b;
        int ctr = 0;
        do {
            b = data[ctr++];
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    public static boolean hasMaskMatch(byte data, byte flag, int... bitmaskPos) {
        char[] bin = padByte(data).toCharArray();
        StringBuilder maskedStr = new StringBuilder();
        boolean putMask = false;
        for (int i = 0; i < bin.length; i++) {
            for (int mask : bitmaskPos) {
                if (!putMask && i == mask) {
                    maskedStr.append(bin[i]);
                    putMask = true;
                }
            }
            if (!putMask) {
                maskedStr.append('0');
            }
            putMask = false;
        }
        return maskedStr.toString().equals(padByte(flag));
    }

    public static boolean hasPlacedBit(byte b, int bitmaskPos) {
        return padByte(b).toCharArray()[bitmaskPos] == 1;
    }

    public static String padByte(byte b) {
        return padByte(b, 8);
    }

    public static String padByte(byte b, int size) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
    public static String padNumber(double value, int decimals) {
        String strVal = String.valueOf(value);
        StringBuilder sb = new StringBuilder(strVal);
        int indPDec = strVal.indexOf('.');
        if (indPDec != -1) {
            sb.append("0".repeat(Math.max(0, decimals - (sb.length() - indPDec - 1))));
        }
        return sb.toString();
    }

    public static double roundTo(double value, int decimals) {
        double pow = Math.pow(10, decimals);
        return Math.round(value * pow) / pow;
    }

    //TODO test that this works
    public static byte[] packBools(boolean[] bools) {
        int len = bools.length;
        int bytes = len >> 3;
        if ((len & 0x07) != 0) {
            bytes++;
        }
        byte[] out = new byte[bytes];
        for (int i = 0; i < bools.length; i++) {
            if (bools[i]) {
                out[i >> 3] |= (byte) reverseByte(1 << (i & 0x07));
            }
        }
        return out;
    }

    // Reverses all the bits in a byte. Used to convert MSB 0 into LSB 0 for button encoding
    public static int reverseByte(int in) {
        in = (in & 0xF0) >> 4 | (in & 0x0F) << 4;
        in = (in & 0xCC) >> 2 | (in & 0x33) << 2;
        in = (in & 0xAA) >> 1 | (in & 0x55) << 1;
        return in;
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
                String rawValue = new String(ArrayUtils.sliceArr(bytes, i, i + n));
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
