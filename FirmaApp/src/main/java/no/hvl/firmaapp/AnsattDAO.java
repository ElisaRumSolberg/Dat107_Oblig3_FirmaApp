package no.hvl.firmaapp;

import jakarta.persistence.*;
import java.util.List;

public class AnsattDAO {

    private EntityManagerFactory emf;

    public AnsattDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Ansatt finnAnsattMedId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Ansatt.class, id);
        } finally {
            em.close();
        }
    }

    public Ansatt finnAnsattMedBrukernavn(String brukernavn) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Ansatt> query = em.createQuery(
                    "SELECT a FROM Ansatt a WHERE a.brukernavn = :bn", Ansatt.class);
            query.setParameter("bn", brukernavn);

            List<Ansatt> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<Ansatt> hentAlleAnsatte() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Ansatt a ORDER BY a.id", Ansatt.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void oppdaterStillingOgLoenn(int id, String nyStilling, double nyLoenn) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Ansatt a = em.find(Ansatt.class, id);
            if (a != null) {
                a.setStilling(nyStilling);
                a.setMaanedsloen(nyLoenn);
            }

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

    public void leggTilAnsatt(Ansatt a) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(a);
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

    public void byttAvdeling(int ansattId, int nyAvdelingId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Ansatt a = em.find(Ansatt.class, ansattId);
            if (a == null) {
                throw new RuntimeException("Ansatt ikke funnet.");
            }

            if (a.getAvdeling() != null && a.getAvdeling().getId() == nyAvdelingId) {
                throw new RuntimeException("Ansatt jobber allerede i denne avdelingen.");
            }

            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(av) FROM Avdeling av WHERE av.sjef IS NOT NULL AND av.sjef.id = :id",
                    Long.class);
            q.setParameter("id", ansattId);

            if (q.getSingleResult() > 0) {
                throw new RuntimeException("Kan ikke bytte avdeling - ansatt er sjef!");
            }

            Avdeling nyAvd = em.find(Avdeling.class, nyAvdelingId);
            if (nyAvd == null) {
                throw new RuntimeException("Avdeling ikke funnet.");
            }

            a.setAvdeling(nyAvd);

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
}