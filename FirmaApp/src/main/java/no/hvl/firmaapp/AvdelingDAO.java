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
}