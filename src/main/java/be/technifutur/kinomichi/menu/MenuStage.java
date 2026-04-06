package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;
import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.person.Person;
import be.technifutur.kinomichi.stage.*;
import be.technifutur.kinomichi.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.InputMismatchException;
import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;

public class MenuStage extends MenuAbstract {
    private final StageService stageService;
    private final Stage stage;

    public MenuStage(Scanner scanner, StageService stageService, Stage stage) {
        super(scanner,
                "Menu Stage",
                stage.getName(),
                "Retour au menu principal");
        this.stageService = stageService;
        this.stage = stage;
    }

    @Override
    protected void displayOptions() {
        printMenuSection("Édition");
        printMenuOption(1, "Afficher '" + stage.getName() + "'", true);
        printMenuOption(2, "Ajouter une session",
                stage.isDraft() &&
                        (stage.canAddSession(DayOfWeek.SATURDAY) || stage.canAddSession(DayOfWeek.SUNDAY)));
        printMenuOption(3, "Supprimer une session", stage.isDraft() && !stage.isSessionsEmpty());
        printMenuOption(4, "Ajouter une activité", stage.isDraft());
        printMenuOption(5, "Supprimer une activité", stage.isDraft() && !stage.isActivitiesEmpty());
        printMenuOption(6, "Modifier le coût maximum", stage.isDraft());

        printMenuSection("Réservations");
        printMenuOption(7, "Ouvrir les réservations", stage.isDraft()
                && !stage.isSessionsEmpty() && !stage.isActivitiesEmpty());
        printMenuOption(8, "Clôturer les réservations", stage.isOpen());

        printMenuSection("Suppression");
        printMenuOption(9, "Supprimer '" + stage.getName() + "'", !stage.isOpen());

    }

    @Override
    protected boolean executeChoice(int choice) {
        switch (choice) {
            case 1 -> {
                printMenuChoice(1, "Afficher '" + stage.getName() + "'");
                System.out.print(stage);
            }

            case 2 -> {
                if (stage.isDraft() && (stage.canAddSession(DayOfWeek.SATURDAY) || stage.canAddSession(DayOfWeek.SUNDAY))) {
                    printMenuChoice(2, "Ajouter une session");
                    addSession();
                } else if (stage.canAddSession(DayOfWeek.SATURDAY) || stage.canAddSession(DayOfWeek.SUNDAY)) {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                } else {
                    printWarning("Option indisponible: Nombre maximum de sessions atteint.");
                }
            }

            case 3 -> {
                if (stage.isDraft() && !stage.isSessionsEmpty()) {
                    printMenuChoice(3, "Supprimer une session");
                    Session session = selectSession();
                    if (session != null) {
                        try {
                            stage.removeSession(session);
                            printSuccess("La session '" + session.getName() + "' a été supprimée !");
                            stageService.save();
                        } catch (KinomichiException e) {
                            printError(e.getMessage());
                        }
                    }
                } else if (!stage.isSessionsEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                } else {
                    printWarning("Option indisponible: Aucune session à supprimer");
                }
            }

            case 4 -> {
                if (stage.isDraft()) {
                    printMenuChoice(4, "Ajouter une activité");
                    addActivity();
                } else {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                }
            }

            case 5 -> {
                if (stage.isDraft() && !stage.isActivitiesEmpty()) {
                    printMenuChoice(5, "Supprimer une activité");
                    Activity activity = selectActivity();
                    if (activity != null) {
                        try {
                            stage.removeActivity(activity);
                            printSuccess("L'activité' '" + activity.getName() + "' a été supprimée !");
                            stageService.save();
                        } catch (KinomichiException e) {
                            printError(e.getMessage());
                        }
                    }
                } else if (!stage.isActivitiesEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                } else {
                    printWarning("Option indisponible: Aucune activité à supprimer");
                }
            }

            case 6 -> {
                if (stage.isDraft()) {
                    printMenuChoice(6, "Modifier le coût maximum");
                    stage.setCappedPrice(askPrice("Coût maximum si le participant fait toutes les sessions (0 pour ignorer) : "));
                    System.out.println();
                    printSuccess("Le coût maximum a été modifié !");
                    stageService.save();
                } else {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                }
            }

            case 7 -> {
                if (stage.isDraft() && !stage.isSessionsEmpty() && !stage.isActivitiesEmpty()) {
                    printMenuChoice(7, "Ouvrir les réservations");
                    stage.open();

                    if (stage.getStatus() == StageStatus.OPEN) {
                        printSuccess("Les réservations ont été ouvertes !");
                        stageService.save();
                    } else {
                        printError("Erreur lors de l'ouverture des réservations !");
                    }
                } else if (!stage.isSessionsEmpty() && !stage.isActivitiesEmpty()) {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                } else {
                    printWarning("Option indisponible: Le stage n'a aucune session et/ou activité.");
                }
            }

            case 8 -> {
                if (stage.isOpen()) {
                    printMenuChoice(8, "Clôturer les réservations");
                    stage.close();

                    if (stage.getStatus() == StageStatus.CLOSED) {
                        printSuccess("Les réservations ont été clôturées !");
                        stageService.save();
                    } else {
                        printError("Erreur lors de la clôture des réservations !");
                    }
                } else {
                    printWarning("Option indisponible: Le stage n'est pas en mode OPEN.");
                }
            }

            case 9 -> {
                if (!stage.isOpen()) {
                    printMenuChoice(9, "Supprimer '" + stage.getName() + "'");
                    if (stageService.remove(stage)) {
                        printSuccess("Le stage '" + stage.getName() + "' a été supprimé !");
                        return false;
                    } else {
                        printError("Erreur lors de la suppression du stage '" + stage.getName() + "' !");
                    }
                } else {
                    printWarning("Option indisponible: Le stage est en mode OPEN.");
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
        return choice >= 0 && choice <= 9;
    }

    private void displaySessions() {
        if (stage.getSessions().isEmpty()) {
            printWarning("Aucune session.");
            return;
        }

        System.out.printf("%-3s %-25s %-25s %-10s %-10s %-10s %-35s%n", "#", "Activité", "Formateur", "Adulte", "Enfant", "Formateur", "Date");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < stage.getSessions().size(); i++) {
            Session session = stage.getSessions().get(i);
            System.out.printf("%-3s %-25s %-25s %-10s %-10s %-10s %-35s%n",
                    BOLD + (i + 1) + RESET + ". ",
                    session.getName(),
                    session.getInstructor().getFullName(),
                    session.getPrice().adult(),
                    session.getPrice().child(),
                    session.getPrice().instructor(),
                    DateUtil.formatDateTime(session.getStartDateTime(), session.getDuration()));
        }
    }

    private void displayActivities() {
        if (stage.getActivities().isEmpty()) {
            printWarning("Aucune activité.");
            return;
        }

        System.out.printf("%-3s %-25s %-10s %-10s %-10s%n", "#", "Activité", "Adulte", "Enfant", "Formateur");
        System.out.println("-------------------------------------------------------------------");

        for (int i = 0; i < stage.getActivities().size(); i++) {
            Activity activity = stage.getActivities().get(i);
            System.out.printf("%-3s %-25s %-10s %-10s %-10s%n",
                    BOLD + (i + 1) + RESET + ". ",
                    activity.getName(),
                    activity.getPrice().adult(),
                    activity.getPrice().child(),
                    activity.getPrice().instructor());
        }
    }

    public Session selectSession() {
        if (stage.getSessions().isEmpty()) {
            printWarning("Aucune session disponible.");
            return null;
        }

        displaySessions();
        System.out.printf("%-3s %-25s%n", BOLD + "0" + RESET + ". ", "Retour");
        System.out.println();
        System.out.print(CYAN + "Choisissez une session (numéro) : " + RESET);

        while (true) {
            try {
                int choice = getScanner().nextInt();
                getScanner().nextLine();

                if (choice < 0 || choice > stage.getSessions().size()) {
                    throw new InvalidMenuChoiceException(String.valueOf(choice));
                }

                if (choice == 0) {
                    return null;
                } else {
                    return stage.getSessions().get(choice - 1);
                }
            } catch (InvalidMenuChoiceException e) {
                printError("Choix invalide !");
            } catch (InputMismatchException e) {
                printError("Veuillez entrer un nombre !");
                getScanner().nextLine();
            }
        }
    }

    public Activity selectActivity() {
        if (stage.getActivities().isEmpty()) {
            printWarning("Aucune activité disponible.");
            return null;
        }

        displayActivities();
        System.out.printf("%-3s %-25s%n", BOLD + "0" + RESET + ". ", "Retour");
        System.out.println();
        System.out.print(CYAN + "Choisissez une activité (numéro) : " + RESET);

        while (true) {
            try {
                int choice = getScanner().nextInt();
                getScanner().nextLine();

                if (choice < 0 || choice > stage.getActivities().size()) {
                    throw new InvalidMenuChoiceException(String.valueOf(choice));
                }

                if (choice == 0) {
                    return null;
                } else {
                    return stage.getActivities().get(choice - 1);
                }
            } catch (InvalidMenuChoiceException e) {
                printError("Choix invalide !");
            } catch (InputMismatchException e) {
                printError("Veuillez entrer un nombre !");
                getScanner().nextLine();
            }
        }
    }

    private void addSession() {
        String name = askSessionName();
        LocalDateTime startDateTime = askStartDateTime();
        int duration = askDuration();
        BigDecimal adult = askPrice("Coût de la session pour un adulte : ");
        BigDecimal child = askPrice("Coût de la session pour un enfant : ");
        BigDecimal instructor = askPrice("Coût de la session pour un formateur : ");
        Price price = new Price(adult, child, instructor);

        Session session = new Session(name, startDateTime, duration, price, new Person(
                "Thomas",
                "Dumez",
                LocalDate.of(1986, 9, 9),
                "test@email.com",
                "123456789",
                "THE-CLUB",
                true
        ));

        System.out.println();
        try {
            stage.addSession(session);
            printSuccess("La session '" + session.getName() + "' a été ajoutée !");
            stageService.save();
        } catch (KinomichiException e) {
            printError(e.getMessage());
        }
    }

    private void addActivity() {
        String name = askActivityName();

        BigDecimal adult = askPrice("Coût de l'activité pour un adulte : ");
        BigDecimal child = askPrice("Coût de l'activité pour un enfant : ");
        BigDecimal instructor = askPrice("Coût de l'activité pour un formateur : ");
        Price price = new Price(adult, child, instructor);

        Activity activity = new Activity(name, price);

        System.out.println();
        try {
            stage.addActivity(activity);
            printSuccess("L'activité '" + activity.getName() + "' a été ajoutée !");
            stageService.save();
        } catch (KinomichiException e) {
            printError(e.getMessage());
        }
    }

    private String askSessionName() {
        while (true) {
            System.out.print("Nom de la session : ");
            String name = getScanner().nextLine().trim();

            if (name.isEmpty()) {
                printWarning("Le nom de la session ne peut pas être vide.");
            } else if (stage.isSessionUnique(name)) {
                return name;
            } else {
                printWarning("La session '" + name + "' existe déjà !");
            }
        }
    }

    private String askActivityName() {
        while (true) {
            System.out.print("Nom de l'activité : ");
            String name = getScanner().nextLine().trim();

            if (name.isEmpty()) {
                printWarning("Le nom de l'activité ne peut pas être vide.");
            } else if (stage.isActivityUnique(name)) {
                return name;
            } else {
                printWarning("L'activité '" + name + "' existe déjà !");
            }
        }
    }

    private DayOfWeek askDay(Stage stage) {
        boolean canSaturday = stage.canAddSession(DayOfWeek.SATURDAY);
        boolean canSunday = stage.canAddSession(DayOfWeek.SUNDAY);

        if (!canSaturday && !canSunday) {
            return null;
        }

        while (true) {
            System.out.println("Jour de la session : ");
            System.out.println((canSaturday ? "" : GRAY) + BOLD + "1" + RESET + (canSaturday ? "" : GRAY) + ". Samedi");
            System.out.println((canSunday ? "" : GRAY) + BOLD + "2" + RESET + (canSaturday ? "" : GRAY) + ". Dimanche");

            System.out.print("Votre choix : ");
            String input = getScanner().nextLine();

            if (input.equals("1") && canSaturday) {
                return DayOfWeek.SATURDAY;
            } else if (input.equals("1")) {
                printWarning("Nombre maximum de sessions atteint le samedi.");
            }

            if (input.equals("2") && canSunday) {
                return DayOfWeek.SUNDAY;
            } else if (input.equals("2")) {
                printWarning("Nombre maximum de sessions atteint le dimanche.");
            }

            printError("Choix invalide !");
        }
    }

    private LocalTime askTime() {
        while (true) {
            System.out.print("Heure de début de la session (entre 09:00 et 17:00 - arrondi à 15 min)(format HH:mm) : ");
            String input = getScanner().nextLine();

            try {
                LocalTime time = LocalTime.parse(input);

                int hour = time.getHour();
                int minute = time.getMinute();

                if (hour < 9 || hour > 17 || (hour == 17 && minute > 0)) {
                    printWarning("L'heure doit être comprise entre 09:00 et 17:00.");
                } else if (minute % 15 != 0) {
                    printWarning("Les minutes doivent être un multiple de 15 (00, 15, 30, 45).");
                } else {
                    return time;
                }
            } catch (Exception e) {
                printWarning("Le format est invalide. Utilisez HH:mm.");
            }
        }
    }

    private LocalDateTime askStartDateTime() {
        DayOfWeek day = askDay(stage);
        if (day == null) {
            return null;
        }

        LocalDate date = stage.getStartDate();

        if (day == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }

        return date.atTime(askTime());
    }

    private int askDuration() {
        while (true) {
            System.out.print("Durée en minute de la session (entre 15 et 120 min - arrondi à 15 min) : ");
            String input = getScanner().nextLine();

            try {
                int duration = (int) Math.round(Integer.parseInt(input) / 15.0) * 15;

                if (duration < 15 || duration > 120) {
                    printWarning("La durée de la session doit être comprise entre 15 et 120 minutes.");
                } else {
                    return duration;
                }
            } catch (NumberFormatException e) {
                printWarning("Le format est invalide. Le nombre doit être entier.");
            }
        }
    }

    private BigDecimal askPrice(String question) {
        while (true) {
            System.out.print(question);
            String input = getScanner().nextLine().trim().replace(",", ".");

            try {
                BigDecimal price = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);

                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    printWarning("La somme doit être positive.");
                } else {
                    return price;
                }
            } catch (NumberFormatException e) {
                printWarning("Le format est invalide. Exemple : 12 ou 12.50");
            }
        }
    }
}
