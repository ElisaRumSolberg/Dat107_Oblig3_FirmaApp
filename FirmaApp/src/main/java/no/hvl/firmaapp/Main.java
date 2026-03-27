package no.hvl.firmaapp;

import jakarta.persistence.*;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("firmaPU");
        EntityManager em = emf.createEntityManager();

        try {
            Ansatt a = em.find(Ansatt.class, 4); // id=4 olan çalışan
            if (a != null) {
                System.out.println("Fant ansatt: " + a);
            } else {
                System.out.println("Ingen ansatt funnet.");
            }
        } finally {
            em.close();
            emf.close();
        }
    }
}