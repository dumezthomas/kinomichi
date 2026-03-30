package be.technifutur.kinomichi.stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StageRepositery {
    private final List<Stage> stages = new ArrayList<>();

    public void add() {
        System.out.println("Création d'un stage");
        Scanner scanner = new Scanner(System.in);

        // Nom
        boolean exist = true;
        String name = "";
        while (exist) {
            System.out.println("Nom du stage :");
            String tempName = scanner.nextLine();
            exist = stages.stream().anyMatch(stage -> stage.getName().equals(tempName));
            if (exist) {
                System.out.println("Ce stage existe déjà! Utilisez un autre nom.");
            } else {
                name = tempName;
            }
        }

        // Description
        System.out.println("Description courte du stage :");
        String shortDescription = scanner.nextLine();

        // Discount
        System.out.println("Pourcentage de réduction (si participe à toutes les plages) :");
        int discount = scanner.nextInt();

        // Ajout
        Stage stage = new Stage(name, shortDescription, discount);
        stages.add(stage);
        System.out.println("Stage ajouté!");
    }

    public List<Stage> get() {
        return stages;
    }
}
