package com.boomaa.opends.util;

import com.boomaa.opends.data.receive.parser.Parser2020;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GameUtils {
    public static byte getAlliance(boolean isBlue, int num) {
        return (byte) (num + (isBlue ? 3 : 0));
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        List<String> data = Files.readAllLines(Paths.get("C:/Users/Nikhil/Desktop/text.txt"));
        for (String d : data) {
            if (!d.isBlank() && !d.isEmpty()) {
                printudp(d);
            }
        }
//        printudp("28 2c 01 04 32 0c 2d 00 22 05 02 42 83 94 5e 00 00 00 00 00 00 00 00 3e ab be 15 42 77 68 81 00 00 00 00 00 00 00 00 3e 96 59 29 ".replaceAll(" ", ""));
//        printudp("0e7b0180320c5000");
//        printudp("0df50100310c9200220502426a1dd200000000000000003e93ec7642559b0900000000000000003e862d0d1a080e0300c0381103c0e00441203c180501100481405014ffaf580a090e2d2d2d2d2d2d2d2d");
//        printudp("02000000450000600a440000801100007f0000017f0000010456047e004ca13600370100200c0000090100000000000000000901000000000000000009010000000000000000090100000000000000000901000000000000000009010000000000000000");
//        printudp("0200000045000060c2680000801100007f0000017f0000010456047e004c700131660106200c0000090100000000000000000901000000000000000009010000000000000000090100000000000000000901000000000000000009010000000000000000");
//        printtcp("008c0b440a3aae001900010000000100001b4c6f6f702074696d65206f6620302e303273206f76657272756e0a005d6564752e7770692e66697273742e7770696c69626a2e497465726174697665526f626f74426173652e7072696e744c6f6f704f76657272756e4d65737361676528497465726174697665526f626f74426173652e6a6176613a323733290000008b0c440a3ab8001a5761726e696e67206174206564752e7770692e66697273742e7770696c69626a2e497465726174697665526f626f74426173652e7072696e744c6f6f704f76657272756e4d65737361676528497465726174697665526f626f74426173652e6a6176613a323733293a204c6f6f702074696d65206f6620302e303273206f76657272756e00070c440a3abd001b");
//        printtcp("00bb0b440a4094001c00010000000100004c4a6f79737469636b20427574746f6e2031206f6e20706f72742030206e6f7420617661696c61626c652c20636865636b20696620636f6e74726f6c6c657220697320706c756767656420696e005b6564752e7770692e66697273742e7770696c69626a2e44726976657253746174696f6e2e7265706f72744a6f79737469636b556e706c75676765645761726e696e672844726976657253746174696f6e2e6a6176613a31313130290000");
//        printtcp("00230c440a5084001e0974656c656f70506572696f64696328293a20302e30323236303473");
    }

    public static void printtcp(String str) {
        Parser2020.RioToDsTcp tcp = new Parser2020.RioToDsTcp(decodeHexString(str));
        if (tcp.getTag().containsKey("Error Code")) {
            System.out.println(tcp.getTag());
        }
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
            System.out.println(udp.getTagSize());
            System.out.println(udp.getTag());
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
