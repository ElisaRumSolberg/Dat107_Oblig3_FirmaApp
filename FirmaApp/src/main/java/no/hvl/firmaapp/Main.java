package no.hvl.firmaapp;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("firmaPU");
        AnsattDAO dao = new AnsattDAO(emf);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== FIRMAMENY ===");
            System.out.println("1. Søk ansatt med ID");
            System.out.println("2. Søk ansatt med brukernavn");
            System.out.println("3. List alle ansatte");
            System.out.println("4. Oppdater stilling og lønn");
            System.out.println("5. Legg til ny ansatt");
            System.out.println("0. Avslutt");
            System.out.print("Valg: ");

            int valg = scanner.nextInt();
            scanner.nextLine();

            switch (valg) {
                case 1 -> {
                    System.out.print("Skriv inn ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    Ansatt a = dao.finnAnsattMedId(id);
                    System.out.println(a != null ? a : "Ikke funnet.");
                }

                case 2 -> {
                    System.out.print("Skriv inn brukernavn: ");
                    String bn = scanner.nextLine();

                    Ansatt a = dao.finnAnsattMedBrukernavn(bn);
                    System.out.println(a != null ? a : "Ikke funnet.");
                }

                case 3 -> {
                    List<Ansatt> alle = dao.hentAlleAnsatte();
                    if (alle.isEmpty()) {
                        System.out.println("Ingen ansatte funnet.");
                    } else {
                        alle.forEach(System.out::println);
                    }
                }

                case 4 -> {
                    System.out.print("ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Ny stilling: ");
                    String stilling = scanner.nextLine();

                    System.out.print("Ny lønn: ");
                    double loenn = scanner.nextDouble();
                    scanner.nextLine();

                    dao.oppdaterStillingOgLoenn(id, stilling, loenn);
                    System.out.println("Oppdatert!");
                }

                case 5 -> {
                    Ansatt ny = new Ansatt();

                    System.out.print("Brukernavn (3-4 bokstaver): ");
                    ny.setBrukernavn(scanner.nextLine());

                    System.out.print("Fornavn: ");
                    ny.setFornavn(scanner.nextLine());

                    System.out.print("Etternavn: ");
                    ny.setEtternavn(scanner.nextLine());

                    ny.setAnsettelsesdato(LocalDate.now());

                    System.out.print("Stilling: ");
                    ny.setStilling(scanner.nextLine());

                    System.out.print("Månedslønn: ");
                    double loenn = scanner.nextDouble();
                    scanner.nextLine();

                    ny.setMaanedsloen(loenn);

                    dao.leggTilAnsatt(ny);
                    System.out.println("Ansatt lagt til!");
                }

                case 0 -> {
                    scanner.close();
                    emf.close();
                    System.out.println("Avslutter...");
                    return;
                }

                default -> System.out.println("Ugyldig valg.");
            }
        }
    }
}