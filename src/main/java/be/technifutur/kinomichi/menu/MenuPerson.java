package be.technifutur.kinomichi.menu;

import be.technifutur.kinomichi.person.Person;
import be.technifutur.kinomichi.person.PersonService;
import be.technifutur.kinomichi.registration.RegistrationService;
import be.technifutur.kinomichi.stage.StageService;

import java.time.LocalDate;
import java.util.Scanner;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;

public class MenuPerson extends MenuAbstract {
    private final StageService stageService;
    private final PersonService personService;
    private final RegistrationService registrationService;
    private final Person person;

    public MenuPerson(Scanner scanner, StageService stageService, PersonService personService, RegistrationService registrationService, Person person) {
        super(scanner,
                "Menu Participant",
                person.getFullName(),
                "Retour au menu principal");
        this.stageService = stageService;
        this.personService = personService;
        this.registrationService = registrationService;
        this.person = person;
    }

    @Override
    protected void displayOptions() {
        printMenuSection("Édition");
        printMenuOption(1, "Afficher '" + person.getFullName() + "'", true);
        printMenuOption(2, "Éditer l'adresse e-mail", true);
        printMenuOption(3, "Éditer le numéro de téléphone", true);
        printMenuOption(4, "Éditer le club de Kinomichi", true);
        printMenuOption(5, "Classifier '" + person.getFullName() + "' comme formateur",
                !person.isInstructor() && !person.isChild(LocalDate.now()));

        printMenuSection("Suppression");
        printMenuOption(6, "Supprimer '" + person.getFullName() + "'", true);
    }

    @Override
    protected boolean executeChoice(int choice) {
        switch (choice) {
            case 1 -> {
                printMenuChoice(1, "Afficher '" + person.getFullName() + "'");
                System.out.print(person);
            }

            case 2 -> {
                printMenuChoice(2, "Éditer l'adresse e-mail");
                System.out.print("E-mail : ");
                String email = getScanner().nextLine();
                person.setEmail(email);
                System.out.println();
                printSuccess("L'adresse e-mail a été modifiée !");
                personService.save();
            }

            case 3 -> {
                printMenuChoice(3, "Éditer le numéro de téléphone");
                System.out.print("Téléphone : ");
                String phone = getScanner().nextLine();
                person.setPhoneNumber(phone);
                System.out.println();
                printSuccess("Le numéro de téléphone a été modifié !");
                personService.save();
            }

            case 4 -> {
                printMenuChoice(4, "Éditer le club de Kinomichi");
                System.out.print("Club de Kinomichi : ");
                String club = getScanner().nextLine();
                person.setClub(club);
                System.out.println();
                printSuccess("Le club de Kinomichi a été modifié !");
                personService.save();
            }

            case 5 -> {
                if (!person.isInstructor() && !person.isChild(LocalDate.now())) {
                    printMenuChoice(5, "Classifier comme formateur");
                    person.becameInstructor();
                    printSuccess("'" + person.getFullName() + "' est maintenant formateur !");
                    personService.save();
                } else if (!person.isChild(LocalDate.now())) {
                    printWarning("Option indisponible: '" + person.getFullName() + "' est déjà formateur.");
                } else {
                    printWarning("Option indisponible: '" + person.getFullName() + "' n'est pas en âge de devenir formateur.");
                }
            }

            case 6 -> {
                printMenuChoice(6, "Supprimer '" + person.getFullName() + "'");
                String instructorString = person.isInstructor() ? "formateur" : "participant";

                int registrations = registrationService.getRegistrationsFiltered(r -> r.getPerson().equals(person)).size();
                if (registrations == 0) {
                    boolean isInstructorFree = stageService.getStagesFiltered(s ->
                            s.getSessions().stream()
                                    .anyMatch(session -> session.getInstructor().equals(person)
                                    )).isEmpty();

                    if (isInstructorFree) {
                        if (personService.remove(person)) {
                            printSuccess("Le " + instructorString + " '" + person.getFullName() + "' a été supprimé !");

                            return false;
                        } else {
                            printError("Erreur lors de la suppression du " + instructorString + " '" + person.getFullName() + "' !");
                        }
                    } else {
                        printError("Erreur lors de la suppression : le formateur est engagé dans certaines sessions.");
                    }
                } else {
                    printError("Erreur lors de la suppression du " + instructorString + " : " + registrations + " réservation(s) enregistrée(s).");
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
}
