package be.technifutur.kinomichi.util;


import java.util.Scanner;

public class ConsoleUtil {
    public static final String RESET = "\u001B[0m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static final String BOLD = "\u001B[1m";

    private static final int NB_DASH = 45;

    private static void printMenuDashLine() {
        System.out.println("+" + "-".repeat(NB_DASH) + "+");
    }

    public static void printMenuTitle(String title) {
        System.out.println();
        printMenuDashLine();
        System.out.println("    " + BOLD + title.toUpperCase() + RESET);
        printMenuDashLine();
    }

    public static void printMenuSection(String section) {
        System.out.println();
        System.out.println(CYAN + "[ " + section.toUpperCase() + " ]" + RESET);
    }

    public static void printMenuOption(int option, String label) {
        System.out.printf(BOLD + " %d" + RESET + ". %s%n", option, label);
    }

    public static void printMenuExit(String quitMessage) {
        System.out.println();
        printMenuDashLine();
        System.out.println(BOLD + " 0" + RESET + ". " + quitMessage);
        printMenuDashLine();
    }

    public static void printMenuPrompt() {
        System.out.println();
        System.out.print(CYAN + "Votre choix : " + RESET);
    }

    public static void printMenuChoice(int option, String label) {
        System.out.println();
        printMenuDashLine();
        System.out.printf(BOLD + " %d" + RESET + ". %s%n", option, label);
        printMenuDashLine();
        System.out.println();
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + message + RESET);
    }

    public static void printPause(Scanner scanner) {
        System.out.println();
        System.out.println(YELLOW + "Appuyez sur Entrée pour continuer..." + RESET);
        scanner.nextLine();
    }
}