package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;

import java.util.InputMismatchException;
import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;

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
            printMenuPrompt();
            choice = readChoice();

            executeChoice(choice);
            if (choice != 0) {
                printPause(scanner);
            }

        } while (choice != 0);
    }

    private int readChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (!isValidChoice(choice)) {
                    throw new InvalidMenuChoiceException(String.valueOf(choice));
                }

                return choice;
            } catch (InvalidMenuChoiceException e) {
                printError("Choix invalide !");
            } catch (InputMismatchException e) {
                printError("Veuillez entrer un nombre !");
                scanner.nextLine();
            }
        }
    }

    private void displayMenu() {
        printMenuTitle(menuTitle);
        displayOptions();
        printMenuExit(quitMessage);
    }

    protected Scanner getScanner() {
        return scanner;
    }

    protected abstract void displayOptions();

    protected abstract void executeChoice(int choice);

    protected abstract boolean isValidChoice(int choice);
}