package be.technifutur.kinomichi.app;

import be.technifutur.kinomichi.io.DataManager;
import be.technifutur.kinomichi.menu.MenuPrincipal;
import be.technifutur.kinomichi.person.PersonService;
import be.technifutur.kinomichi.stage.StageService;

import java.util.Scanner;

public class Main {
    static void main() {
        DataManager dataManager = new DataManager("data/");

        StageService stageService = new StageService(dataManager);
        PersonService personService = new PersonService(dataManager);

        Scanner scanner = new Scanner(System.in);
        MenuPrincipal menu = new MenuPrincipal(scanner, stageService, personService);
        menu.show();
    }
}
