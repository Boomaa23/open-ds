package com.boomaa.opends.headless;

import java.util.Scanner;

public class HeadlessController {
    private static final Scanner inputScanner = new Scanner(System.in);
    public static final String LOGO_ASCII = ""
        + "   ____                   ____  _____\n"
        + "  / __ \\____  ___  ____  / __ \\/ ___/\n"
        + " / / / / __ \\/ _ \\/ __ \\/ / / /\\__ \\ \n"
        + "/ /_/ / /_/ /  __/ / / / /_/ /___/ / \n"
        + "\\____/ .___/\\___/_/ /_/_____//____/  \n"
        + "    /_/                              \n";

    private HeadlessController() {
    }

    public static void start() {
        printMenu();
    }

    public static void printMenu() {
        System.out.println(LOGO_ASCII
            + "by Boomaa23\n"
            + "------------------\n"
            + "Status: \n"
            + "\n"
            + "Actions:\n");
    }

    public static String prompt(String message) {
        System.out.println(message);
        return getInput();
    }

    public static String getInput() {
        return inputScanner.nextLine();
    }
}
