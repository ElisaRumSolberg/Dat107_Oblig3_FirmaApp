package no.hvl.firmaapp;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static int lesHeltall(Scanner scanner, String melding) {
        while (true) {
            System.out.print(melding);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig input. Skriv inn et heltall.");
            }
        }
    }

    private static double lesDesimaltall(Scanner scanner, String melding) {
        while (true) {
            System.out.print(melding);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig input. Skriv inn et tall.");
            }
        }
    }

    private static String lesBrukernavn(Scanner scanner) {
        while (true) {
            System.out.print("Brukernavn (3-4 bokstaver): ");
            String input = scanner.nextLine().trim();
            if (input.length() >= 3 && input.length() <= 4 && input.matches("[a-zA-Z]+")) {
                return input.toLowerCase();
            }
            System.out.println("Ugyldig brukernavn. Kun bokstaver, 3-4 tegn.");
        }
    }

    private static String lesStilling(Scanner scanner) {
        List<String> gyldige = List.of(
                "Utvikler", "Seniorutvikler", "Designer",
                "Tester", "Analytiker", "Prosjektleder", "Leder"
        );
        while (true) {
            System.out.println("Gyldige stillinger:");
            for (int i = 0; i < gyldige.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + gyldige.get(i));
            }
            int valg = lesHeltall(scanner, "Velg stilling (1-" + gyldige.size() + "): ");
            if (valg >= 1 && valg <= gyldige.size()) {
                return gyldige.get(valg - 1);
            }
            System.out.println("Ugyldig valg.");
        }
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("firmaPU");
        AnsattDAO ansattDao = new AnsattDAO(emf);
        AvdelingDAO avdelingDao = new AvdelingDAO(emf);
        ProsjektDAO prosjektDao = new ProsjektDAO(emf);
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
            System.out.println("9. Opprett ny avdeling med sjef");
            System.out.println("10. Legg til nytt prosjekt");
            System.out.println("11. Registrer prosjektdeltagelse");
            System.out.println("12. Før timer for ansatt på prosjekt");
            System.out.println("13. Vis prosjektinfo");
            System.out.println("0. Avslutt");

            int valg = lesHeltall(scanner, "Valg: ");

            switch (valg) {

                case 1 -> {
                    int id = lesHeltall(scanner, "Skriv inn ID: ");
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
                        System.out.println("\n--- Ansatte ---");
                        System.out.printf("%-6s %-22s %-15s %-14s %-12s %s%n",
                                "ID", "Navn", "Brukernavn", "Stilling", "Lønn", "Avdeling");
                        System.out.println("-".repeat(85));
                        for (Ansatt a : alle) {
                            System.out.printf("%-6s %-22s %-15s %-14s %,-10.0f %s%n",
                                    "[" + a.getId() + "]",
                                    a.getFornavn() + " " + a.getEtternavn(),
                                    "(" + a.getBrukernavn() + ")",
                                    a.getStilling(),
                                    a.getMaanedsloen(),
                                    a.getAvdeling() != null ? a.getAvdeling().getNavn() : "Ingen");
                        }
                        System.out.println("-".repeat(85));
                    }
                }

                case 4 -> {
                    int id = lesHeltall(scanner, "ID: ");
                    System.out.print("Ny stilling: ");
                    String stilling = scanner.nextLine();
                    double loenn = lesDesimaltall(scanner, "Ny lønn: ");
                    ansattDao.oppdaterStillingOgLoenn(id, stilling, loenn);
                    System.out.println("Oppdatert!");
                }

                case 5 -> {
                    Ansatt ny = new Ansatt();
                    ny.setBrukernavn(lesBrukernavn(scanner));
                    System.out.print("Fornavn: ");
                    ny.setFornavn(scanner.nextLine());
                    System.out.print("Etternavn: ");
                    ny.setEtternavn(scanner.nextLine());
                    ny.setAnsettelsesdato(LocalDate.now());
                    ny.setStilling(lesStilling(scanner));
                    double loenn = lesDesimaltall(scanner, "Månedslønn: ");
                    ny.setMaanedsloen(loenn);

                    List<Avdeling> avdelinger = avdelingDao.hentAlleAvdelinger();
                    Avdeling avdeling = null;
                    while (avdeling == null) {
                        System.out.println("Tilgjengelige avdelinger:");
                        for (Avdeling av : avdelinger) {
                            System.out.println("  " + av.getId() + ". " + av.getNavn());
                        }
                        int avdId = lesHeltall(scanner, "Avdeling ID: ");
                        avdeling = avdelingDao.finnAvdelingMedId(avdId);
                        if (avdeling == null) {
                            System.out.println("Avdeling ikke funnet! Prøv igjen.");
                        }
                    }
                    ny.setAvdeling(avdeling);
                    ansattDao.leggTilAnsatt(ny);
                    System.out.println("Ansatt lagt til!");
                }

                case 6 -> {
                    int id = lesHeltall(scanner, "Avdeling ID: ");
                    Avdeling av = avdelingDao.finnAvdelingMedId(id);
                    if (av != null) {
                        System.out.println(av);
                        System.out.println("Ansatte:");
                        if (av.getAnsatte().isEmpty()) {
                            System.out.println("  Ingen ansatte i avdelingen.");
                        } else {
                            for (Ansatt a : av.getAnsatte()) {
                                boolean erSjef = av.getSjef() != null &&
                                        av.getSjef().getId() == a.getId();
                                System.out.println("  " + a + (erSjef ? " [SJEF]" : ""));
                            }
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
                    int aid = lesHeltall(scanner, "Ansatt ID: ");
                    int avdId = lesHeltall(scanner, "Ny avdeling ID: ");
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
                    int sjefId = lesHeltall(scanner, "Sjef (ansatt ID): ");
                    try {
                        Avdeling ny = avdelingDao.opprettAvdelingMedSjef(navn, sjefId);
                        System.out.println("Avdeling opprettet: " + ny);
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
                }

                case 10 -> {
                    Prosjekt ny = new Prosjekt();
                    System.out.print("Navn: ");
                    ny.setNavn(scanner.nextLine());
                    System.out.print("Beskrivelse: ");
                    ny.setBeskrivelse(scanner.nextLine());
                    try {
                        prosjektDao.leggTilProsjekt(ny);
                        System.out.println("Prosjekt lagt til!");
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
                }

                case 11 -> {
                    int aid = lesHeltall(scanner, "Ansatt ID: ");
                    int pid = lesHeltall(scanner, "Prosjekt ID: ");

                    System.out.print("Rolle: ");
                    String rolle = scanner.nextLine();

                    int timer = lesHeltall(scanner, "Timer: ");

                    if (timer < 0) {
                        System.out.println("Feil: Timer kan ikke være negativ.");
                        break;
                    }

                    try {
                        prosjektDao.leggTilDeltagelse(aid, pid, rolle, timer);
                        System.out.println("Deltagelse registrert!");
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
                }

                case 12 -> {
                    int aid = lesHeltall(scanner, "Ansatt ID: ");
                    int pid = lesHeltall(scanner, "Prosjekt ID: ");
                    int timer = lesHeltall(scanner, "Antall timer å legge til: ");

                    if (timer < 0) {
                        System.out.println("Feil: Timer kan ikke være negativ.");
                        break;
                    }

                    try {
                        prosjektDao.leggTilTimer(aid, pid, timer);
                        System.out.println("Timer oppdatert!");
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
                }

                case 13 -> {
                    int pid = lesHeltall(scanner, "Prosjekt ID: ");
                    Prosjekt p = prosjektDao.finnProsjektMedId(pid);
                    if (p == null) {
                        System.out.println("Prosjekt ikke funnet.");
                    } else {
                        System.out.println(p);
                        if (p.getDeltagere().isEmpty()) {
                            System.out.println("Ingen deltagere registrert.");
                        } else {
                            int totalt = 0;
                            for (Prosjektdeltagelse pd : p.getDeltagere()) {
                                System.out.println("  " + pd);
                                totalt += pd.getTimer();
                            }
                            System.out.println("Totalt timer: " + totalt);
                        }
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