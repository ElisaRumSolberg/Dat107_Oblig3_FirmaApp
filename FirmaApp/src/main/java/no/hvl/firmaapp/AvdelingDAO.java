package no.hvl.firmaapp;

import jakarta.persistence.*;
import java.util.List;

public class AvdelingDAO {

    private EntityManagerFactory emf;

    public AvdelingDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Avdeling finnAvdelingMedId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Avdeling> query = em.createQuery(
                    "SELECT DISTINCT av FROM Avdeling av " +
                            "LEFT JOIN FETCH av.sjef " +
                            "LEFT JOIN FETCH av.ansatte " +
                            "WHERE av.id = :id", Avdeling.class);
            query.setParameter("id", id);

            List<Avdeling> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<Avdeling> hentAlleAvdelinger() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT av FROM Avdeling av " +
                                    "LEFT JOIN FETCH av.sjef " +
                                    "ORDER BY av.id", Avdeling.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void leggTilAvdeling(Avdeling av) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(av);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Avdeling opprettAvdelingMedSjef(String navn, int sjefId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Ansatt sjef = em.find(Ansatt.class, sjefId);
            if (sjef == null) {
                throw new RuntimeException("Ansatt ikke funnet.");
            }

            // sjekke allerede er det sjef
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(av) FROM Avdeling av WHERE av.sjef IS NOT NULL AND av.sjef.id = :id",
                    Long.class);
            q.setParameter("id", sjefId);

            if (q.getSingleResult() > 0) {
                throw new RuntimeException("Ansatt er allerede sjef i en annen avdeling!");
            }

            // lage ny avdeling
            Avdeling ny = new Avdeling();
            ny.setNavn(navn);
            ny.setSjef(sjef);

            em.persist(ny);
            em.flush(); // ID

           // ny sjef flyttte ny avdeling
            sjef.setAvdeling(ny);

            tx.commit();
            return ny;

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}