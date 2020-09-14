package com.boomaa.opends.util;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.util.battery.BatteryInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class GameTesting {
    public static void main(String[] args) throws URISyntaxException, IOException {
        byte[] data = decodeHexString("");
        System.out.println();
    }

    public static void printtcp(String str) {
        Parser2020.RioToDsTcp tcp = new Parser2020.RioToDsTcp(decodeHexString(str));
//        if (tcp.getTags().containsKey("Error Code")) {
//            System.out.println(tcp.getTags());
//        }
    }

    public static void printudp(String str) {
        Parser2020.RioToDsUdp udp = new Parser2020.RioToDsUdp(decodeHexString(str));

//        if (udp.getTag() != null && udp.getTag().values().iterator().next() instanceof Float && !udp.getTag().containsKey("PDP Port 00")) {
            System.out.println(sepHex(str));
            System.out.println(udp.getSequenceNum());
            System.out.println(udp.getCommVersion());
            System.out.println(udp.getStatus());
            System.out.println(udp.getTrace());
            System.out.println(udp.getBatteryVoltage());
            System.out.println(udp.isRequestingDate());
//            System.out.println(udp.getTagSize());
            System.out.println(udp.getTags());
            System.out.println();
//        }
    }

    private static String sepHex(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            sb.append(hex.charAt(i));
            sb.append(hex.charAt(i + 1));
            sb.append(' ');
        }
        return sb.toString();
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
}
