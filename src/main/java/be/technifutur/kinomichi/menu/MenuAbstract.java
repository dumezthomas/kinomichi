package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;

import java.util.InputMismatchException;
import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;

public abstract class MenuAbstract {
    private final Scanner scanner;
    private final String title;
    private final String subtitle;
    private final String backOrQuit;

    public MenuAbstract(Scanner scanner, String title, String subtitle, String backOrQuit) {
        this.scanner = scanner;
        this.title = title;
        this.subtitle = subtitle;
        this.backOrQuit = backOrQuit;
    }

    public void show() {
        boolean stayInMenu;

        do {
            displayMenu();
            printMenuPrompt();
            int choice = readChoice();

            stayInMenu = executeChoice(choice);
            if (stayInMenu) {
                printPause(scanner);
            }

        } while (stayInMenu);
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
        printMenuTitle(title, subtitle);
        displayOptions();
        printMenuExit(backOrQuit);
    }

    protected Scanner getScanner() {
        return scanner;
    }

    protected abstract void displayOptions();

    protected abstract boolean executeChoice(int choice);

    protected abstract boolean isValidChoice(int choice);
}