package be.technifutur.kinomichi.util;


import java.util.Scanner;

public class ConsoleUI {
    public static final String RESET = "\u001B[0m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static final String BOLD = "\u001B[1m";

    private static final int WIDTH = 41;

    private static void printMenuDashLine() {
        System.out.println("+" + "-".repeat(WIDTH - 2) + "+");
    }

    public static void printMenuTitle(String title) {
        printMenuDashLine();
        System.out.println(" " + title);
        printMenuDashLine();
    }

    public static void printMenuSection(String title) {
        System.out.println();
        System.out.println(CYAN + "[ " + title + " ]" + RESET);
    }

    public static void printMenuOption(int option, String label) {
        System.out.printf(" %d. %s%n", option, label);
    }

    public static void printMenuExit(String quitMessage) {
        System.out.println();
        printMenuDashLine();
        System.out.println(" 0. " + quitMessage);
        printMenuDashLine();
    }

    public static void printMenuPrompt() {
        System.out.print("\nVotre choix : ");
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
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
}