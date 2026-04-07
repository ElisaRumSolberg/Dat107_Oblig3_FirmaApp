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
                    Ansatt a = null;
                    while (a == null) {
                        int id = lesHeltall(scanner, "Skriv inn ID (0 for å avbryte): ");
                        if (id == 0) break;
                        a = ansattDao.finnAnsattMedId(id);
                        if (a == null) {
                            System.out.println("Ansatt ikke funnet! Prøv igjen.");
                        }
                    }
                    if (a != null) {
                        System.out.println(a);
                        System.out.println("Avdeling: " +
                                (a.getAvdeling() != null ? a.getAvdeling().getNavn() : "Ingen"));
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
                    Avdeling av = null;
                    while (av == null) {
                        int id = lesHeltall(scanner, "Avdeling ID (0 for å avbryte): ");
                        if (id == 0) break;
                        av = avdelingDao.finnAvdelingMedId(id);
                        if (av == null) {
                            System.out.println("Avdeling ikke funnet! Prøv igjen.");
                        }
                    }
                    if (av != null) {
                        System.out.println("\n--- " + av.getNavn() + " ---");
                        System.out.println("Sjef: " + (av.getSjef() != null ?
                                av.getSjef().getFornavn() + " " + av.getSjef().getEtternavn() : "Ingen"));
                        System.out.println("-".repeat(60));
                        if (av.getAnsatte().isEmpty()) {
                            System.out.println("  Ingen ansatte i avdelingen.");
                        } else {
                            System.out.printf("%-6s %-25s %-15s %s%n", "ID", "Navn", "Stilling", "Lønn");
                            System.out.println("-".repeat(60));
                            for (Ansatt a : av.getAnsatte()) {
                                boolean erSjef = av.getSjef() != null &&
                                        av.getSjef().getId() == a.getId();
                                System.out.printf("%-6s %-25s %-15s %,.0f kr %s%n",
                                        "[" + a.getId() + "]",
                                        a.getFornavn() + " " + a.getEtternavn(),
                                        a.getStilling(),
                                        a.getMaanedsloen(),
                                        erSjef ? "[SJEF]" : "");
                            }
                            System.out.println("-".repeat(60));
                        }
                    }
                }

                case 7 -> {
                    List<Avdeling> alle = avdelingDao.hentAlleAvdelinger();
                    if (alle.isEmpty()) {
                        System.out.println("Ingen avdelinger funnet.");
                    } else {
                        System.out.println("\n--- Alle avdelinger ---");
                        System.out.printf("%-6s %-20s %s%n", "ID", "Navn", "Sjef");
                        System.out.println("-".repeat(50));
                        for (Avdeling av : alle) {
                            String sjefNavn = av.getSjef() != null ?
                                    av.getSjef().getFornavn() + " " + av.getSjef().getEtternavn() : "Ingen";
                            System.out.printf("%-6s %-20s %s%n",
                                    "[" + av.getId() + "]",
                                    av.getNavn(),
                                    sjefNavn);
                        }
                        System.out.println("-".repeat(50));
                    }
                }

                case 8 -> {
                    // 1. Ansattları göster
                    System.out.println("\nAnsatte:");
                    List<Ansatt> alleAnsatte = ansattDao.hentAlleAnsatte();
                    for (Ansatt a : alleAnsatte) {
                        boolean erSjef = a.getAvdeling() != null &&
                                a.getAvdeling().getSjef() != null &&
                                a.getAvdeling().getSjef().getId() == a.getId();
                        System.out.printf("  %-6s %-25s %-15s %s%n",
                                "[" + a.getId() + "]",
                                a.getFornavn() + " " + a.getEtternavn(),
                                a.getAvdeling() != null ? "(" + a.getAvdeling().getNavn() + ")" : "(Ingen avdeling)",
                                erSjef ? "[SJEF]" : "");
                    }

                    // 2. Ansatt ID sor
                    Ansatt ansatt = null;
                    int aid = 0;
                    while (ansatt == null) {
                        aid = lesHeltall(scanner, "Ansatt ID (0 for å avbryte): ");
                        if (aid == 0) break;
                        ansatt = ansattDao.finnAnsattMedId(aid);
                        if (ansatt == null) {
                            System.out.println("Ansatt ikke funnet! Prøv igjen.");
                        }
                    }

                    if (ansatt != null) {
                        // 3. Avdelingleri göster
                        System.out.println("\nTilgjengelige avdelinger:");
                        List<Avdeling> avdelinger = avdelingDao.hentAlleAvdelinger();
                        for (Avdeling av : avdelinger) {
                            System.out.printf("  %-6s %s%n", "[" + av.getId() + "]", av.getNavn());
                        }

                        // 4. Avdeling ID sor
                        Avdeling nyAvd = null;
                        while (nyAvd == null) {
                            int avdId = lesHeltall(scanner, "Ny avdeling ID (0 for å avbryte): ");
                            if (avdId == 0) break;
                            nyAvd = avdelingDao.finnAvdelingMedId(avdId);
                            if (nyAvd == null) {
                                System.out.println("Avdeling ikke funnet! Prøv igjen.");
                            } else {
                                try {
                                    ansattDao.byttAvdeling(aid, avdId);
                                    System.out.println("Avdeling byttet!");
                                } catch (Exception e) {
                                    System.out.println("Feil: " + e.getMessage());
                                    break;
                                }
                            }
                        }
                    }
                }

                case 9 -> {
                    System.out.print("Navn på ny avdeling: ");
                    String navn = scanner.nextLine();

                    // Ansattları göster
                    System.out.println("\nAnsatte:");
                    List<Ansatt> alleAnsatte = ansattDao.hentAlleAnsatte();
                    for (Ansatt a : alleAnsatte) {
                        boolean erSjef = a.getAvdeling() != null &&
                                a.getAvdeling().getSjef() != null &&
                                a.getAvdeling().getSjef().getId() == a.getId();
                        System.out.printf("  %-6s %-25s %-15s %s%n",
                                "[" + a.getId() + "]",
                                a.getFornavn() + " " + a.getEtternavn(),
                                a.getAvdeling() != null ? "(" + a.getAvdeling().getNavn() + ")" : "(Ingen avdeling)",
                                erSjef ? "[SJEF]" : "");
                    }

                    boolean ferdig = false;
                    while (!ferdig) {
                        int sjefId = lesHeltall(scanner, "Sjef (ansatt ID, 0 for å avbryte): ");
                        if (sjefId == 0) break;
                        Ansatt sjef = ansattDao.finnAnsattMedId(sjefId);
                        if (sjef == null) {
                            System.out.println("Ansatt ikke funnet! Prøv igjen.");
                            continue;
                        }
                        try {
                            Avdeling ny = avdelingDao.opprettAvdelingMedSjef(navn, sjefId);
                            System.out.println("Avdeling opprettet: " + ny);
                            ferdig = true;
                        } catch (Exception e) {
                            System.out.println("Feil: " + e.getMessage() + " Velg en annen ansatt.");
                        }
                    }
                }

                case 10 -> {
                    Prosjekt ny = new Prosjekt();

                    String navn = "";
                    while (navn.isBlank()) {
                        System.out.print("Navn: ");
                        navn = scanner.nextLine().trim();
                        if (navn.isBlank()) {
                            System.out.println("Navn kan ikke være tomt!");
                        }
                    }
                    ny.setNavn(navn);

                    System.out.print("Beskrivelse: ");
                    ny.setBeskrivelse(scanner.nextLine().trim());

                    try {
                        prosjektDao.leggTilProsjekt(ny);
                        System.out.println("Prosjekt lagt til!");
                    } catch (Exception e) {
                        System.out.println("Feil: " + e.getMessage());
                    }
                }

                case 11 -> {
                    // 1. Ansatt list
                    System.out.println("\nAnsatte:");
                    List<Ansatt> alleAnsatte = ansattDao.hentAlleAnsatteMedProsjekter();
                    for (Ansatt a : alleAnsatte) {
                        String prosjekter = a.getDeltagelser().isEmpty() ? "Ingen prosjekter" :
                                a.getDeltagelser().stream()
                                        .map(d -> d.getProsjekt().getNavn())
                                        .collect(java.util.stream.Collectors.joining(", "));
                        System.out.printf("  %-6s %-25s %s%n",
                                "[" + a.getId() + "]",
                                a.getFornavn() + " " + a.getEtternavn(),
                                prosjekter);
                    }

                    // 2. spær Ansatt ID
                    Ansatt ansatt = null;
                    int aid = 0;
                    while (ansatt == null) {
                        aid = lesHeltall(scanner, "Ansatt ID (0 for å avbryte): ");
                        if (aid == 0) break;
                        ansatt = ansattDao.finnAnsattMedId(aid);
                        if (ansatt == null) {
                            System.out.println("Ansatt ikke funnet! Prøv igjen.");
                        }
                    }

                    if (ansatt != null) {
                        // 3. Prosjekt listesi
                        System.out.println("\nProsjekter:");
                        List<Prosjekt> alleProsjekter = prosjektDao.hentAlleProsjekter();
                        for (Prosjekt p : alleProsjekter) {
                            System.out.printf("  %-6s %s%n",
                                    "[" + p.getId() + "]",
                                    p.getNavn());
                        }

                        // 4. spør Prosjekt ID
                        Prosjekt prosjekt = null;
                        int pid = 0;
                        while (prosjekt == null) {
                            pid = lesHeltall(scanner, "Prosjekt ID (0 for å avbryte): ");
                            if (pid == 0) break;
                            prosjekt = prosjektDao.finnProsjektMedId(pid);
                            if (prosjekt == null) {
                                System.out.println("Prosjekt ikke funnet! Prøv igjen.");
                            }
                        }

                        if (prosjekt != null) {
                            // 5. spør Rolle
                            String rolle = "";
                            while (rolle.isBlank() || rolle.matches(".*\\d.*")) {
                                System.out.print("Rolle: ");
                                rolle = scanner.nextLine().trim();
                                if (rolle.isBlank()) {
                                    System.out.println("Rolle kan ikke være tom!");
                                } else if (rolle.matches(".*\\d.*")) {
                                    System.out.println("Rolle kan ikke inneholde tall!");
                                }
                            }

                            // 6. Timer sor
                            int timer = -1;
                            while (timer <= 0) {
                                timer = lesHeltall(scanner, "Timer: ");
                                if (timer <= 0) {
                                    System.out.println("Timer må være større enn 0!");
                                }
                            }

                            // 7. Kaydet
                            try {
                                prosjektDao.leggTilDeltagelse(aid, pid, rolle, timer);
                                System.out.println("Deltagelse registrert!");
                            } catch (Exception e) {
                                System.out.println("Feil: " + e.getMessage());
                            }
                        }
                    }
                }

                case 12 -> {
                    // Ansatt listesi
                    System.out.println("\nAnsatte:");
                    List<Ansatt> alleAnsatte = ansattDao.hentAlleAnsatteMedProsjekter();
                    for (Ansatt a : alleAnsatte) {
                        String prosjekter = a.getDeltagelser().isEmpty() ? "Ingen prosjekter" :
                                a.getDeltagelser().stream()
                                        .map(d -> d.getProsjekt().getNavn())
                                        .collect(java.util.stream.Collectors.joining(", "));
                        System.out.printf("  %-6s %-25s %s%n",
                                "[" + a.getId() + "]",
                                a.getFornavn() + " " + a.getEtternavn(),
                                prosjekter);
                    }

                    // Ansatt ID sor
                    Ansatt ansatt = null;
                    int aid = 0;
                    while (ansatt == null) {
                        aid = lesHeltall(scanner, "Ansatt ID (0 for å avbryte): ");
                        if (aid == 0) break;
                        ansatt = ansattDao.finnAnsattMedId(aid);
                        if (ansatt == null) {
                            System.out.println("Ansatt ikke funnet! Prøv igjen.");
                        }
                    }

                    if (ansatt != null) {
                        // Prosjekt listesi
                        System.out.println("\nProsjekter:");
                        List<Prosjekt> alleProsjekter = prosjektDao.hentAlleProsjekter();
                        for (Prosjekt p : alleProsjekter) {
                            System.out.printf("  %-6s %s%n",
                                    "[" + p.getId() + "]",
                                    p.getNavn());
                        }

                        // spør Prosjekt ID

                        Prosjekt valgtProsjekt = null;
                        int pid = 0;
                        while (valgtProsjekt == null) {
                            pid = lesHeltall(scanner, "Prosjekt ID (0 for å avbryte): ");
                            if (pid == 0) break;

                            Prosjekt p = prosjektDao.finnProsjektMedId(pid);
                            if (p == null) {
                                System.out.println("Prosjekt ikke funnet! Prøv igjen.");
                            } else if (!prosjektDao.erRegistrertPaProsjekt(aid, pid)) {
                                System.out.println("Feil: Ansatt er ikke registrert på dette prosjektet! Prøv igjen.");
                            } else {
                                valgtProsjekt = p;
                            }
                        }

                        if (valgtProsjekt != null) {
                            // Timer sor
                            int timer = -1;
                            while (timer <= 0) {
                                timer = lesHeltall(scanner, "Antall timer å legge til: ");
                                if (timer <= 0) {
                                    System.out.println("Timer må være større enn 0!");
                                }
                            }
                            try {
                                prosjektDao.leggTilTimer(aid, pid, timer);
                                System.out.println("Timer oppdatert!");
                            } catch (Exception e) {
                                System.out.println("Feil: " + e.getMessage());
                            }
                        }
                    }
                }

                case 13 -> {
                    // Prosjekt list
                    System.out.println("\nProsjekter:");
                    List<Prosjekt> alleProsjekter = prosjektDao.hentAlleProsjekter();
                    for (Prosjekt pr : alleProsjekter) {
                        System.out.printf("  %-6s %s%n",
                                "[" + pr.getId() + "]",
                                pr.getNavn());
                    }

                    // spør Prosjekt ID
                    Prosjekt p = null;
                    while (p == null) {
                        int pid = lesHeltall(scanner, "Prosjekt ID (0 for å avbryte): ");
                        if (pid == 0) break;
                        p = prosjektDao.finnProsjektMedId(pid);
                        if (p == null) {
                            System.out.println("Prosjekt ikke funnet! Prøv igjen.");
                        }
                    }

                    if (p != null) {
                        System.out.println("\n--- " + p.getNavn() + " ---");
                        System.out.println("Beskrivelse: " + p.getBeskrivelse());
                        System.out.println("-".repeat(60));

                        if (p.getDeltagere().isEmpty()) {
                            System.out.println("Ingen deltagere registrert.");
                        } else {
                            System.out.printf("%-25s %-15s %s%n", "Navn", "Rolle", "Timer");
                            System.out.println("-".repeat(60));
                            int totalt = 0;
                            for (Prosjektdeltagelse pd : p.getDeltagere()) {
                                System.out.printf("%-25s %-15s %d timer%n",
                                        pd.getAnsatt().getFornavn() + " " + pd.getAnsatt().getEtternavn(),
                                        pd.getRolle(),
                                        pd.getTimer());
                                totalt += pd.getTimer();
                            }
                            System.out.println("-".repeat(60));
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