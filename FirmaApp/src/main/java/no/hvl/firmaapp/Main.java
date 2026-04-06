package no.hvl.firmaapp;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("firmaPU");
        AnsattDAO ansattDao = new AnsattDAO(emf);
        AvdelingDAO avdelingDao = new AvdelingDAO(emf);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== FIRMAMENY ===");
            System.out.println("1. Søk ansatt med ID");
            System.out.println("2. Søk ansatt med brukernavn");
            System.out.println("3. List alle ansatte");
            System.out.println("4. Oppdater stilling og lønn");
            System.out.println("5. Legg til ny ansatt");
            System.out.println("6. Søk avdeling med ID");
            System.out.println("7. List alle avdelinger");
            System.out.println("8. Bytt avdeling for ansatt");
            System.out.println("9. Legg til ny avdeling");
            System.out.println("0. Avslutt");
            System.out.print("Valg: ");

            int valg = scanner.nextInt();
            scanner.nextLine();

            switch (valg) {

                case 1 -> {
                    System.out.print("Skriv inn ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Ansatt a = ansattDao.finnAnsattMedId(id);
                    if (a != null) {
                        System.out.println(a);
                        System.out.println("Avdeling: " +
                                (a.getAvdeling() != null ? a.getAvdeling().getNavn() : "Ingen"));
                    } else {
                        System.out.println("Ikke funnet.");
                    }
                }

                case 2 -> {
                    System.out.print("Skriv inn brukernavn: ");
                    String bn = scanner.nextLine();
                    Ansatt a = ansattDao.finnAnsattMedBrukernavn(bn);
                    if (a != null) {
                        System.out.println(a);
                        System.out.println("Avdeling: " +
                                (a.getAvdeling() != null ? a.getAvdeling().getNavn() : "Ingen"));
                    } else {
                        System.out.println("Ikke funnet.");
                    }
                }

                case 3 -> {
                    List<Ansatt> alle = ansattDao.hentAlleAnsatte();
                    if (alle.isEmpty()) {
                        System.out.println("Ingen ansatte funnet.");
                    } else {
                        for (Ansatt a : alle) {
                            System.out.println(a + " | Avdeling: " +
                                    (a.getAvdeling() != null ? a.getAvdeling().getNavn() : "Ingen"));
                        }
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
                    ansattDao.oppdaterStillingOgLoenn(id, stilling, loenn);
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
                    System.out.print("Avdeling ID: ");
                    int avdId = scanner.nextInt();
                    scanner.nextLine();
                    Avdeling avdeling = avdelingDao.finnAvdelingMedId(avdId);
                    if (avdeling == null) {
                        System.out.println("Avdeling ikke funnet!");
                    } else {
                        ny.setAvdeling(avdeling);
                        ansattDao.leggTilAnsatt(ny);
                        System.out.println("Ansatt lagt til!");
                    }
                }

                case 6 -> {
                    System.out.print("Avdeling ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Avdeling av = avdelingDao.finnAvdelingMedId(id);
                    if (av != null) {
                        System.out.println(av);
                        System.out.println("Ansatte:");
                        for (Ansatt a : av.getAnsatte()) {
                            boolean erSjef = av.getSjef() != null &&
                                    av.getSjef().getId() == a.getId();
                            System.out.println("  " + a + (erSjef ? " [SJEF]" : ""));
                        }
                    } else {
                        System.out.println("Avdeling ikke funnet.");
                    }
                }

                case 7 -> {
                    List<Avdeling> alle = avdelingDao.hentAlleAvdelinger();
                    if (alle.isEmpty()) {
                        System.out.println("Ingen avdelinger funnet.");
                    } else {
                        alle.forEach(System.out::println);
                    }
                }
                case 8 -> {
                    System.out.print("Ansatt ID: ");
                    int aid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Ny avdeling ID: ");
                    int avdId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        ansattDao.byttAvdeling(aid, avdId);
                        System.out.println("Avdeling byttet!");
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
                }

                case 9 -> {
                    System.out.print("Navn på ny avdeling: ");
                    String navn = scanner.nextLine();
                    System.out.print("Sjef (ansatt ID): ");
                    int sjefId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        Avdeling ny = avdelingDao.opprettAvdelingMedSjef(navn, sjefId);
                        System.out.println("Avdeling opprettet: " + ny);
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
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