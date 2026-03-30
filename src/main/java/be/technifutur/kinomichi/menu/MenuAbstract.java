package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.util.ConsoleUI;

import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUI.*;

public abstract class MenuAbstract {
    private final Scanner scanner;
    private final String menuTitle;
    private final String quitMessage;

    public MenuAbstract(Scanner scanner, String menuTitle, String quitMessage) {
        this.scanner = scanner;
        this.menuTitle = menuTitle;
        this.quitMessage = quitMessage;
    }

    public void show() {
        int choice;

        do {
            displayMenu();
            ConsoleUI.printMenuPrompt();
            choice = readInt();

            if (isValidChoice(choice)) {
                executeChoice(choice);
                if (choice != 0) {
                    printPause(scanner);
                }
            } else {
                printError("Option invalide !");
            }
        } while (choice != 0);
    }

    private int readInt() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                return choice;
            } catch (Exception e) {
                printError("Entrée invalide, entrez un nombre !");
                scanner.nextLine();
            }
        }
    }

    private void displayMenu() {
        printMenuTitle(menuTitle);
        displayOptions();
        printMenuExit(quitMessage);
    }

    protected abstract void displayOptions();

    protected abstract void executeChoice(int choice);

    protected abstract boolean isValidChoice(int choice);
}