package com.boomaa.opends.util;

import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.usb.USBInterface;
import com.boomaa.opends.usb.input.Component;
import com.boomaa.opends.usb.input.DIDeviceObject;
import com.boomaa.opends.usb.input.DirectInput;
import com.boomaa.opends.usb.input.DirectInputDevice;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GameTesting {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
//        byte[] data = decodeHexString("0e0300c0381103c0e00441203c180501100481405014ffac59");
//        System.out.println(ReceiveTag.PDP_LOG.getAction().getValue(data));
//        long ct = System.currentTimeMillis() / 1000;
//        long val = -2_212_122_495L + ct;
//        byte[] data = NumberUtils.intToByteQuad((int) val);
//        System.out.println(NumberUtils.getUInt32(data));
//        System.out.println(val);
//        System.out.println(ct);
//        System.out.println(Arrays.toString(data));
//        System.out.println(NumberUtils.getUInt32(decodeHexString("dac124f3")));

//        System.out.println();
        DirectInputDevice d = new DirectInput().getDevices().get(0);
        int ctr = 0;
        while (true) {
            d.poll();
//            ctr += d.getComponents()[0].getValue();
            System.out.println(d.getComponents()[0].getValue());
//            for (Component c : d.getComponents()) {
//                if (((DIDeviceObject)c).isAxis())
//                    System.out.println(c.getName() + ": " + c.getValue());
//            }
            Thread.sleep(500);
        }
//        2_082_844_800L + 4_294_967_295L
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
