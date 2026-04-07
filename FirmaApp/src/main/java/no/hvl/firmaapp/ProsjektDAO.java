package no.hvl.firmaapp;

import jakarta.persistence.*;
import java.util.List;

public class ProsjektDAO {

    private EntityManagerFactory emf;

    public ProsjektDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Prosjekt finnProsjektMedId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Prosjekt> q = em.createQuery(
                    "SELECT DISTINCT p FROM Prosjekt p " +
                            "LEFT JOIN FETCH p.deltagere d " +
                            "LEFT JOIN FETCH d.ansatt " +
                            "WHERE p.id = :id", Prosjekt.class);
            q.setParameter("id", id);

            List<Prosjekt> results = q.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<Prosjekt> hentAlleProsjekter() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Prosjekt p ORDER BY p.id", Prosjekt.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void leggTilProsjekt(Prosjekt p) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(p);
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

    public void leggTilDeltagelse(int ansattId, int prosjektId, String rolle, int timer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Ansatt ansatt = em.find(Ansatt.class, ansattId);
            if (ansatt == null) {
                throw new RuntimeException("Ansatt ikke funnet.");
            }

            Prosjekt prosjekt = em.find(Prosjekt.class, prosjektId);
            if (prosjekt == null) {
                throw new RuntimeException("Prosjekt ikke funnet.");
            }

            ProsjektdeltagelseId pid = new ProsjektdeltagelseId(ansattId, prosjektId);
            Prosjektdeltagelse eksisterende = em.find(Prosjektdeltagelse.class, pid);
            if (eksisterende != null) {
                throw new RuntimeException("Ansatt er allerede registrert på dette prosjektet.");
            }

            Prosjektdeltagelse pd = new Prosjektdeltagelse(ansatt, prosjekt, rolle, timer);
            em.persist(pd);

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

    public void leggTilTimer(int ansattId, int prosjektId, int ekstraTimer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (ekstraTimer < 0) {
                throw new RuntimeException("Timer kan ikke være negativ.");
            }

            ProsjektdeltagelseId pid = new ProsjektdeltagelseId(ansattId, prosjektId);
            Prosjektdeltagelse pd = em.find(Prosjektdeltagelse.class, pid);

            if (pd == null) {
                throw new RuntimeException("Deltagelse ikke funnet.");
            }

            pd.setTimer(pd.getTimer() + ekstraTimer);

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

    public boolean erRegistrertPaProsjekt(int ansattId, int prosjektId) {
        EntityManager em = emf.createEntityManager();
        try {
            ProsjektdeltagelseId pid = new ProsjektdeltagelseId(ansattId, prosjektId);
            return em.find(Prosjektdeltagelse.class, pid) != null;
        } finally {
            em.close();
        }
    }
}