package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;
import be.technifutur.kinomichi.stage.Stage;
import be.technifutur.kinomichi.stage.StageService;
import be.technifutur.kinomichi.stage.StageStatus;
import be.technifutur.kinomichi.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;

public class MenuPrincipal extends MenuAbstract {
    private final StageService stageService;

    public MenuPrincipal(Scanner scanner, StageService stageService) {
        super(scanner,
                "Système de gestion des stages KINOMICHI",
                "Quitter l'application");
        this.stageService = stageService;
    }

    @Override
    protected void displayOptions() {
        printMenuSection("Stages");
        printMenuOption(1, "Afficher les stages");
        printMenuOption(2, "Afficher un stage");
        printMenuOption(3, "Créer un stage");
        printMenuOption(4, "Modifier un stage (DRAFT)");
        printMenuOption(5, "Supprimer un stage (DRAFT ou CLOSED)");
        printMenuOption(6, "Ouvrir les réservations (DRAFT)");
        printMenuOption(7, "Clôturer les réservations (OPEN)");

        printMenuSection("Participants");
        printMenuOption(8, "Créer un participant/formateur");
    }

    @Override
    protected void executeChoice(int choice) {
        switch (choice) {
            case 1 -> {
                printMenuChoice(1, "Afficher les stages");
                displayStages(stageService.getStagesSorted(Comparator.comparing(Stage::getStartDate)));
            }

            case 2 -> {
                printMenuChoice(2, "Afficher un stage");
                Stage stage = selectStage(
                        Comparator.comparing(Stage::getStartDate),
                        null);
                if (stage != null) {
                    System.out.println(stage);
                }
            }

            case 3 -> {
                printMenuChoice(3, "Créer un stage");
                addStage();
            }

            case 4 -> {
                printMenuChoice(4, "Modifier un stage (DRAFT)");
                Stage stage = selectStage(
                        Comparator.comparing(Stage::getStartDate),
                        Stage::isDraft);
                if (stage != null) {
                    System.out.println(stage);
                }
            }

            case 5 -> {
                printMenuChoice(5, "Supprimer un stage (DRAFT ou CLOSED)");
                Stage stage = selectStage(
                        Comparator.comparing(Stage::getStartDate),
                        s -> !s.isOpen());
                if (stage != null) {
                    System.out.println();
                    if (stageService.remove(stage)) {
                        printSuccess("Le stage '" + stage.getName() + "' a été supprimé !");
                    } else {
                        printError("Erreur lors de la suppression du stage '" + stage.getName() + "' !");
                    }
                }
            }

            case 6 -> {
                printMenuChoice(6, "Ouvrir les réservations (DRAFT)");
                Stage stage = selectStage(
                        Comparator.comparing(Stage::getStartDate),
                        Stage::isDraft);
                if (stage != null) {
                    stage.open();

                    System.out.println();
                    if (stage.getStatus() == StageStatus.OPEN) {
                        printSuccess("Les réservations ont été ouvertes !");
                        stageService.save();
                    } else {
                        printError("Erreur lors de l'ouverture des réservations !");
                    }
                }
            }

            case 7 -> {
                printMenuChoice(7, "Clôturer les réservations (OPEN)");
                Stage stage = selectStage(
                        Comparator.comparing(Stage::getStartDate),
                        Stage::isOpen);
                if (stage != null) {
                    stage.close();

                    System.out.println();
                    if (stage.getStatus() == StageStatus.CLOSED) {
                        printSuccess("Les réservations ont été clôturées !");
                        stageService.save();
                    } else {
                        printError("Erreur lors de la clôture des réservations !");
                    }
                }
            }

            case 0 -> {
                System.out.println("Au revoir !");
            }
        }
    }

    @Override
    protected boolean isValidChoice(int choice) {
        return choice >= 0 && choice <= 7;
    }

    private void displayStages(List<Stage> stages) {
        if (stages.isEmpty()) {
            printWarning("Aucun stage.");
            return;
        }

        System.out.printf("%-3s %-25s %-10s %-15s%n", "#", "Nom du stage", "Statut", "Date de début");
        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < stages.size(); i++) {
            Stage stage = stages.get(i);

            System.out.printf("%-3s %-25s %-10s %-15s%n",
                    BOLD + (i + 1) + RESET + ". ",
                    stage.getName(),
                    stage.getStatus(),
                    DateUtil.format(stage.getStartDate()));
        }
    }

    public Stage selectStage(Comparator<Stage> comparator, Predicate<Stage> filter) {
        List<Stage> stages = filter == null ?
                stageService.getStagesSorted(comparator) :
                stageService.getStagesSortedAndFiltered(comparator, filter);

        if (stages.isEmpty()) {
            printWarning("Aucun stage disponible.");
            return null;
        }

        displayStages(stages);
        System.out.printf("%-3s %-25s%n", BOLD + "0" + RESET + ". ", "Retour");
        System.out.println();
        System.out.print(CYAN + "Choisissez un stage (numéro) : " + RESET);

        while (true) {
            try {
                int choice = getScanner().nextInt();
                getScanner().nextLine();

                if (choice < 0 || choice > stages.size()) {
                    throw new InvalidMenuChoiceException(String.valueOf(choice));
                }

                if (choice == 0) {
                    return null;
                } else {
                    return stages.get(choice - 1);
                }
            } catch (InvalidMenuChoiceException e) {
                printError("Choix invalide !");
            } catch (InputMismatchException e) {
                printError("Veuillez entrer un nombre !");
                getScanner().nextLine();
            }
        }
    }

    private void addStage() {
        String name = askName();

        System.out.print("Courte description du stage : ");
        String shortDescription = getScanner().nextLine();

        LocalDate startDate = askStartDate();

        BigDecimal cappedPrice = askCappedPrice();

        Stage stage = new Stage(name, shortDescription, startDate, cappedPrice);

        System.out.println();
        if (stageService.add(stage)) {
            printSuccess("Le stage '" + stage.getName() + "' a été ajouté !");
        } else {
            printError("Erreur lors de l'ajout du stage '" + stage.getName() + "' !");
        }
    }

    private String askName() {
        while (true) {
            System.out.print("Nom du stage : ");
            String name = getScanner().nextLine().trim();

            if (name.isEmpty()) {
                printWarning("Le nom du stage ne peut pas être vide.");
            } else if (stageService.isExist(name)) {
                printWarning("Le stage '" + name + "' existe déjà !");
            } else {
                return name;
            }
        }
    }

    private LocalDate askStartDate() {
        while (true) {
            System.out.print("Date du premier jour du stage (un samedi)(format: DD-MM-YYYY) : ");
            String input = getScanner().nextLine();

            try {
                LocalDate date = DateUtil.parse(input);

                if (!DateUtil.isTodayOrFuture(date)) {
                    printWarning("La date entrée est passée.");
                } else if (!DateUtil.isSaturday(date)) {
                    printWarning("La date entrée n'est pas un samedi.");
                } else {
                    return date;
                }
            } catch (Exception e) {
                printWarning("Le format est invalide. Utilisez DD-MM-YYYY.");
            }
        }
    }

    private BigDecimal askCappedPrice() {
        while (true) {
            System.out.print("Coût maximum si le participant fait toutes les sessions (0 pour ignorer) : ");
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
