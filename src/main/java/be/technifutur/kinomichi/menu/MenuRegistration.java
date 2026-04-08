package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;
import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.registration.Registration;
import be.technifutur.kinomichi.registration.RegistrationService;
import be.technifutur.kinomichi.stage.Activity;
import be.technifutur.kinomichi.stage.Session;
import be.technifutur.kinomichi.util.DateUtil;

import java.util.InputMismatchException;
import java.util.List;
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
        printMenuOption(2, "S'inscrire à une session",
                registration.getStage().isOpen() && !registration.getAvailableSessions().isEmpty());
        printMenuOption(3, "Se désinscrire d'une session",
                registration.getStage().isOpen() && !registration.getSessions().isEmpty());
        printMenuOption(4, "S'inscrire à une activité",
                registration.getStage().isOpen() && !registration.getAvailableActivities().isEmpty());
        printMenuOption(5, "Se désinscrire d'une activité",
                registration.getStage().isOpen() && !registration.getActivities().isEmpty());

        printMenuSection("Suppression");
        printMenuOption(6, "Supprimer '" + registration.getName() + "'", registration.getStage().isOpen());
    }

    @Override
    protected boolean executeChoice(int choice) {
        switch (choice) {
            case 1 -> {
                printMenuChoice(1, "Afficher '" + registration.getName() + "'");
                System.out.print(registration);
            }

            case 2 -> {
                if (registration.getStage().isOpen() && !registration.getAvailableSessions().isEmpty()) {
                    printMenuChoice(2, "S'inscrire à une session");
                    Session session = selectSession(registration.getAvailableSessions());
                    if (session != null) {
                        try {
                            registration.registerSession(session);
                            printSuccess("L'inscription à la session '" + session.getName() + "' a été enregistrée !");
                            registrationService.save();
                        } catch (KinomichiException e) {
                            printError(e.getMessage());
                        }
                    }
                } else if (!registration.getAvailableSessions().isEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode OPEN.");
                } else {
                    printWarning("Option indisponible: Le participant est inscrit à toute les sessions disponibles.");
                }
            }

            case 3 -> {
                if (registration.getStage().isOpen() && !registration.getSessions().isEmpty()) {
                    printMenuChoice(3, "Se désinscrire d'une session");
                    Session session = selectSession(registration.getSessions());
                    if (session != null) {
                        try {
                            registration.unregisterSession(session);
                            System.out.println();
                            printSuccess("L'inscription à la session '" + session.getName() + "' a été supprimée !");
                            registrationService.save();
                        } catch (KinomichiException e) {
                            printError(e.getMessage());
                        }
                    }
                } else if (!registration.getActivities().isEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode OPEN.");
                } else {
                    printWarning("Option indisponible: Le participant n'est inscrit à aucune sessions.");
                }
            }

            case 4 -> {
                if (registration.getStage().isOpen() && !registration.getAvailableActivities().isEmpty()) {
                    printMenuChoice(4, "S'inscrire à une activité");
                    Activity activity = selectActivity(registration.getAvailableActivities());
                    if (activity != null) {
                        try {
                            registration.registerActivity(activity);
                            System.out.println();
                            printSuccess("L'inscription à l'activité '" + activity.getName() + "' a été enregistrée !");
                            registrationService.save();
                        } catch (KinomichiException e) {
                            printError(e.getMessage());
                        }
                    }
                } else if (!registration.getAvailableActivities().isEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode OPEN.");
                } else {
                    printWarning("Option indisponible: Le participant est inscrit à toute les activités disponibles.");
                }
            }

            case 5 -> {
                if (registration.getStage().isOpen() && !registration.getActivities().isEmpty()) {
                    printMenuChoice(5, "Se désinscrire d'une activité");
                    Activity activity = selectActivity(registration.getActivities());
                    if (activity != null) {
                        try {
                            registration.unregisterActivity(activity);
                            System.out.println();
                            printSuccess("L'inscription à l'activité '" + activity.getName() + "' a été supprimée !");
                            registrationService.save();
                        } catch (KinomichiException e) {
                            printError(e.getMessage());
                        }
                    }
                } else if (!registration.getActivities().isEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode OPEN.");
                } else {
                    printWarning("Option indisponible: Le participant n'est inscrit à aucune activités.");
                }
            }

            case 6 -> {
                if (registration.getStage().isOpen()) {
                    printMenuChoice(6, "Supprimer '" + registration.getName() + "'");
                    if (registrationService.remove(registration)) {
                        printSuccess("La réservation '" + registration.getName() + "' a été supprimée !");
                        return false;
                    } else {
                        printError("Erreur lors de la suppression de la réservation '" + registration.getName() + "' !");
                    }
                } else {
                    printWarning("Option indisponible: Le stage n'est pas en mode OPEN.");
                }
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
        return choice >= 0 && choice <= 6;
    }

    private void displaySessions(List<Session> sessions) {
        if (sessions.isEmpty()) {
            printWarning("Aucune session disponible.");
            return;
        }

        System.out.printf("%-3s %-25s %-25s %-10s %-35s%n", "#", "Activité", "Formateur", "Coût", "Date");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            System.out.printf("%-3s %-25s %-25s %-10s %-35s%n",
                    BOLD + (i + 1) + RESET + ". ",
                    session.getName(),
                    session.getInstructor().getFullName(),
                    session.getPrice().forPerson(registration.getPerson(), registration.getStage().getStartDate()) + "€",
                    DateUtil.formatDateTime(session.getStartDateTime(), session.getDuration()));
        }
    }

    private void displayActivities(List<Activity> activities) {
        if (activities.isEmpty()) {
            printWarning("Aucune activité disponible.");
            return;
        }

        System.out.printf("%-3s %-25s %-10s%n", "#", "Activité", "Coût");
        System.out.println("-------------------------------------------------------------------");

        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            System.out.printf("%-3s %-25s %-10s%n",
                    BOLD + (i + 1) + RESET + ". ",
                    activity.getName(),
                    activity.getPrice().forPerson(registration.getPerson(), registration.getStage().getStartDate()) + "€");
        }
    }

    public Session selectSession(List<Session> sessions) {
        if (sessions.isEmpty()) {
            printWarning("Aucune session disponible.");
            return null;
        }

        displaySessions(sessions);
        System.out.printf("%-3s %-25s%n", BOLD + "0" + RESET + ". ", "Retour");
        System.out.println();
        System.out.print(CYAN + "Choisissez une session (numéro) : " + RESET);

        while (true) {
            try {
                int choice = getScanner().nextInt();
                getScanner().nextLine();

                if (choice < 0 || choice > sessions.size()) {
                    throw new InvalidMenuChoiceException(String.valueOf(choice));
                }

                if (choice == 0) {
                    return null;
                } else {
                    return sessions.get(choice - 1);
                }
            } catch (InvalidMenuChoiceException e) {
                printError("Choix invalide !");
            } catch (InputMismatchException e) {
                printError("Veuillez entrer un nombre !");
                getScanner().nextLine();
            }
        }
    }

    public Activity selectActivity(List<Activity> activities) {
        if (activities.isEmpty()) {
            printWarning("Aucune activité disponible.");
            return null;
        }

        displayActivities(activities);
        System.out.printf("%-3s %-25s%n", BOLD + "0" + RESET + ". ", "Retour");
        System.out.println();
        System.out.print(CYAN + "Choisissez une activité (numéro) : " + RESET);

        while (true) {
            try {
                int choice = getScanner().nextInt();
                getScanner().nextLine();

                if (choice < 0 || choice > activities.size()) {
                    throw new InvalidMenuChoiceException(String.valueOf(choice));
                }

                if (choice == 0) {
                    return null;
                } else {
                    return activities.get(choice - 1);
                }
            } catch (InvalidMenuChoiceException e) {
                printError("Choix invalide !");
            } catch (InputMismatchException e) {
                printError("Veuillez entrer un nombre !");
                getScanner().nextLine();
            }
        }
    }
}
