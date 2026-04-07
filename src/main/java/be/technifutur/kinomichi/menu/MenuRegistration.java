package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.registration.Registration;
import be.technifutur.kinomichi.registration.RegistrationService;

import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;

public class MenuRegistration extends MenuAbstract {
    private final RegistrationService registrationService;
    private Registration registration;

    public MenuRegistration(Scanner scanner, RegistrationService registrationService, Registration registration) {
        super(scanner,
                "Menu Réservation",
                registration.getName(),
                "Retour au menu principal");
        this.registrationService = registrationService;
        this.registration = registration;
    }

    @Override
    protected void displayOptions() {
        printMenuSection("Édition");
        printMenuOption(1, "Afficher '" + registration.getName() + "'", true);
        printMenuOption(2, "S'inscrire à une session", registration.getStage().isOpen());
        printMenuOption(3, "Se désinscrire d'une session", registration.getStage().isOpen());
        printMenuOption(4, "S'inscrire à une activité", registration.getStage().isOpen());
        printMenuOption(5, "Se désinscrire d'une activité", registration.getStage().isOpen());
        printMenuOption(6, "Payer la réservation", !registration.isPaid());

        printMenuSection("Suppression");
        printMenuOption(7, "Supprimer '" + registration.getName() + "'", registration.getStage().isOpen());
    }

    @Override
    protected boolean executeChoice(int choice) {
        switch (choice) {
            case 1 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 2 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 3 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 4 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 5 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 6 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 7 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 0 -> {
                printMenuChoice(0, "Retour au menu principal");
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean isValidChoice(int choice) {
        return choice >= 0 && choice <= 1;
    }
}
