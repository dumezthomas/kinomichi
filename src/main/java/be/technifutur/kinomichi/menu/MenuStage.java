package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.exception.InvalidMenuChoiceException;
import be.technifutur.kinomichi.stage.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        printMenuOption(2, "Ajouter une session", stage.isDraft());
        printMenuOption(3, "Supprimer une session", stage.isDraft() && !stage.isSessionsEmpty());
        printMenuOption(4, "Ajouter une activité", stage.isDraft());
        printMenuOption(5, "Supprimer une activité", stage.isDraft() && !stage.isActivitiesEmpty());
        printMenuOption(6, "Modification du coût maximum", stage.isDraft());

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
                System.out.println(stage);
            }

            case 2 -> {
                if (stage.isDraft()) {
                    printMenuChoice(2, "Ajouter une session");
                    addSession();
                } else {
                    printWarning("Option indisponible: Le stage n'est pas en mode DRAFT.");
                }
            }

            case 3 -> {
                if (stage.isDraft() && !stage.isSessionsEmpty()) {
                    printMenuChoice(3, "Supprimer une session");
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
                        if (stage.removeActivity(activity)) {
                            printSuccess("L'activité' '" + activity.getName() + "' a été supprimée !");
                            stageService.save();
                        } else {
                            printError("Erreur lors de la suppression de l'activité '" + activity.getName() + "' !");
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
                    printMenuChoice(6, "Modification du coût maximum");
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
                    printMenuChoice(9, "Supprimer un stage");
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

    private void addActivity() {
        String name = askActivityName();

        BigDecimal adult = askPrice("Coût de l'activité pour un adulte : ");
        BigDecimal child = askPrice("Coût de l'activité pour un enfant : ");
        BigDecimal instructor = askPrice("Coût de l'activité pour un formateur : ");
        Price price = new Price(adult, child, instructor);

        Activity activity = new Activity(name, price);

        System.out.println();
        if (stage.addActivity(activity)) {
            printSuccess("L'activité '" + activity.getName() + "' a été ajoutée !");
            stageService.save();
        } else {
            printError("Erreur lors de l'ajout de l'activité '" + activity.getName() + "' !");
        }
    }

    private void addSession() {
        String name = askActivityName();

        BigDecimal adult = askPrice("Coût de l'activité pour un adulte : ");
        BigDecimal child = askPrice("Coût de l'activité pour un enfant : ");
        BigDecimal instructor = askPrice("Coût de l'activité pour un formateur : ");
        Price price = new Price(adult, child, instructor);

        Activity activity = new Activity(name, price);

        System.out.println();
        if (stage.addActivity(activity)) {
            printSuccess("L'activité '" + activity.getName() + "' a été ajoutée !");
            stageService.save();
        } else {
            printError("Erreur lors de l'ajout de l'activité '" + activity.getName() + "' !");
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
