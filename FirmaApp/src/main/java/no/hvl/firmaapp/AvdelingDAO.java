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
            return em.find(Avdeling.class, id);
        } finally {
            em.close();
        }
    }

    public List<Avdeling> hentAlleAvdelinger() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT av FROM Avdeling av ORDER BY av.id", Avdeling.class)
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
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}