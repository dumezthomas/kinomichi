package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;
import be.technifutur.kinomichi.stage.Stage;
import be.technifutur.kinomichi.stage.StageService;
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
                "Menu Principal",
                null,
                "Quitter l'application");
        this.stageService = stageService;
    }

    @Override
    protected void displayOptions() {
        printMenuSection("Stages");
        printMenuOption(1, "Afficher les stages", !stageService.isStagesEmpty());
        printMenuOption(2, "Afficher un stage", !stageService.isStagesEmpty());
        printMenuOption(3, "Créer un stage", true);
        printMenuOption(4, "Éditer un stage", !stageService.isStagesEmpty());

        printMenuSection("Participants");
        printMenuOption(5, "Afficher les participants/formateurs", false);
        printMenuOption(6, "Afficher un participant/formateur", false);
        printMenuOption(7, "Ajouter un participant/formateur", false);
        printMenuOption(9, "Éditer un participant/formateur", false);

        printMenuSection("Réservations");
        printMenuOption(10, "Afficher les réservations", false);
        printMenuOption(11, "Afficher une réservation", false);
        printMenuOption(12, "Prendre une réservation", false);
        printMenuOption(13, "Modifier une réservation", false);
    }

    @Override
    protected boolean executeChoice(int choice) {
        switch (choice) {
            case 1 -> {
                if (!stageService.isStagesEmpty()) {
                    printMenuChoice(1, "Afficher les stages");
                    displayStages(stageService.getStagesSorted(Comparator.comparing(Stage::getStartDate)));
                } else {
                    printWarning("Option indisponible: Aucun stage à afficher.");
                }
            }

            case 2 -> {
                if (!stageService.isStagesEmpty()) {
                    printMenuChoice(2, "Afficher un stage");
                    Stage stage = selectStage(
                            Comparator.comparing(Stage::getStartDate),
                            null);
                    if (stage != null) {
                        System.out.println();
                        System.out.println(stage);
                    }
                } else {
                    printWarning("Option indisponible: Aucun stage à afficher.");
                }
            }

            case 3 -> {
                printMenuChoice(3, "Créer un stage");
                Stage stage = addStage();
                if (stage != null) {
                    MenuStage menu = new MenuStage(getScanner(), stageService, stage);
                    menu.show();
                }
            }

            case 4 -> {
                if (!stageService.isStagesEmpty()) {
                    printMenuChoice(4, "Éditer un stage");
                    Stage stage = selectStage(
                            Comparator.comparing(Stage::getStartDate),
                            null);
                    if (stage != null) {
                        MenuStage menu = new MenuStage(getScanner(), stageService, stage);
                        menu.show();
                    }
                } else {
                    printWarning("Option indisponible: Aucun stage à éditer.");
                }
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

            case 8 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 9 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 10 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 11 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 12 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 13 -> {
                printWarning("Option indisponible: Pas encore implémenté.");
            }

            case 0 -> {
                printMenuChoice(0, "Quitter l'application");
                System.out.println("Au revoir !");
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean isValidChoice(int choice) {
        return choice >= 0 && choice <= 13;
    }

    private void displayStages(List<Stage> stages) {
        if (stages.isEmpty()) {
            printWarning("Aucun stage.");
            return;
        }

        System.out.printf("%-3s %-25s %-10s %-12s %-12s %-35s%n", "#", "Stage", "Statut", "Sessions", "Activités", "Dates");
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < stages.size(); i++) {
            Stage stage = stages.get(i);

            System.out.printf("%-3s %-25s %-10s %-12s %-12s %-35s%n",
                    BOLD + (i + 1) + RESET + ". ",
                    stage.getName(),
                    stage.getStatus(),
                    stage.getSessions().size(),
                    stage.getActivities().size(),
                    DateUtil.formatWeekend(stage.getStartDate()));
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

    private Stage addStage() {
        String name = askName();

        System.out.print("Courte description du stage : ");
        String shortDescription = getScanner().nextLine();

        LocalDate startDate = askStartDate();
        BigDecimal cappedPrice = askCappedPrice();
        Stage stage = new Stage(name, shortDescription, startDate, cappedPrice);

        System.out.println();
        if (stageService.add(stage)) {
            printSuccess("Le stage '" + stage.getName() + "' a été ajouté !");
            return stage;
        } else {
            printError("Erreur lors de l'ajout du stage '" + stage.getName() + "' !");
            return null;
        }
    }

    private String askName() {
        while (true) {
            System.out.print("Nom du stage : ");
            String name = getScanner().nextLine().trim();

            if (name.isEmpty()) {
                printWarning("Le nom du stage ne peut pas être vide.");
            } else if (stageService.isStageUnique(name)) {
                return name;
            } else {
                printWarning("Le stage '" + name + "' existe déjà !");
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
