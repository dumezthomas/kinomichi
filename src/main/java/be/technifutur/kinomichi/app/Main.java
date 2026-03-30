package be.technifutur.kinomichi.app;

import be.technifutur.kinomichi.menu.MenuPrincipal;

import java.util.Scanner;

public class Main {
    static void main() {
        Scanner scanner = new Scanner(System.in);
        MenuPrincipal menu = new MenuPrincipal(scanner);
        menu.show();
    }
}
