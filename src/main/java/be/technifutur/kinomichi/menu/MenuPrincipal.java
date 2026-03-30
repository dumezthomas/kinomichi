package be.technifutur.kinomichi.menu;

import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUI.printMenuOption;
import static be.technifutur.kinomichi.util.ConsoleUI.printMenuSection;

public class MenuPrincipal extends MenuAbstract {

    public MenuPrincipal(Scanner scanner) {
        super(scanner,
                "Système de gestion des stages KINOMICHI",
                "Quitter l'application");
    }

    @Override
    protected void displayOptions() {
        printMenuSection("STAGES");
        printMenuOption(1, "Afficher un stage et ses participants");
        printMenuOption(2, "Créer un stage");
        printMenuOption(3, "Modifier un stage");
        printMenuOption(4, "Ouvrir les réservations");
        printMenuOption(5, "Clôturer les réservations");

        printMenuSection("PARTICIPANTS");
        printMenuOption(6, "Afficher un participant/formateur");
        printMenuOption(7, "Créer un participant/formateur");
        printMenuOption(8, "Modifier un participant/formateur");

        printMenuSection("INSCRIPTIONS");
        printMenuOption(9, "S'inscrire");
    }

    @Override
    protected void executeChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Afficher un stage...");
                break;
            case 2:
                System.out.println("Créer un stage...");
                break;
            case 3:
                System.out.println("Modifier un stage...");
                break;
            case 4:
                System.out.println("Ouvrir les réservations...");
                break;
            case 5:
                System.out.println("Clôturer les réservations...");
                break;
            case 6:
                System.out.println("Afficher participant...");
                break;
            case 7:
                System.out.println("Créer participant...");
                break;
            case 8:
                System.out.println("Modifier participant...");
                break;
            case 9:
                System.out.println("Inscription...");
                break;
            case 0:
                System.out.println("Au revoir !");
                break;
        }
    }

    @Override
    protected boolean isValidChoice(int choice) {
        return choice >= 0 && choice <= 9;
    }
}
