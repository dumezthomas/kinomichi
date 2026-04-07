package be.technifutur.kinomichi.util;


import java.util.Scanner;

public class ConsoleUtil {
    public static final String RESET = "\u001B[0m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String GRAY = "\033[0;90m";

    public static final String BOLD = "\u001B[1m";

    private static final int NB_DASH = 75;

    private static void printMenuDashLine() {
        System.out.println("+" + "-".repeat(NB_DASH) + "+");
    }

    public static void printMenuTitle(String title, String subtitle) {
        String subtitleString = "";
        if (subtitle != null && !subtitle.isEmpty()) {
            subtitleString = " : " + subtitle;
        }

        System.out.println();
        printMenuDashLine();
        System.out.println(BOLD + "  KINOMICHI " + CYAN + "[ " + title.toUpperCase() + " ]" + RESET + subtitleString);
        printMenuDashLine();
    }

    public static void printMenuSection(String section) {
        System.out.println();
        System.out.println(CYAN + "[ " + section.toUpperCase() + " ]" + RESET);
    }

    public static void printMenuOption(int option, String label, boolean enabled) {
        String optionString = "";
        if (option < 10) {
            optionString = " ";
        }

        if (enabled) {
            System.out.printf(BOLD + optionString + " %d" + RESET + ". %s%n", option, label);
        } else {
            System.out.printf(GRAY + BOLD + optionString + " %d" + RESET + GRAY + ". %s%n" + RESET, option, label);
        }
    }

    public static void printMenuExit(String backOrQuit) {
        System.out.println();
        printMenuDashLine();
        System.out.println(BOLD + "  0" + RESET + ". " + backOrQuit);
        printMenuDashLine();
    }

    public static void printMenuPrompt() {
        System.out.println();
        System.out.print(CYAN + "Votre choix : " + RESET);
    }

    public static void printMenuChoice(int option, String label) {
        String optionString = "";
        if (option < 10) {
            optionString = " ";
        }

        System.out.println();
        printMenuDashLine();
        System.out.printf(BOLD + optionString + " %d" + RESET + ". %s%n", option, label);
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