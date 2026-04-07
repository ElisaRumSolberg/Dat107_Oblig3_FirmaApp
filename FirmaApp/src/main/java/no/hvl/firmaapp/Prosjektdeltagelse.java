package no.hvl.firmaapp;

import jakarta.persistence.*;

@Entity
@Table(name = "prosjektdeltagelse")
public class Prosjektdeltagelse {

    @EmbeddedId
    private ProsjektdeltagelseId id = new ProsjektdeltagelseId();

    @ManyToOne
    @MapsId("ansattId")
    @JoinColumn(name = "ansatt_id")
    private Ansatt ansatt;

    @ManyToOne
    @MapsId("prosjektId")
    @JoinColumn(name = "prosjekt_id")
    private Prosjekt prosjekt;

    @Column(nullable = false, length = 50)
    private String rolle;

    @Column(nullable = false)
    private int timer;

    public Prosjektdeltagelse() {
    }

    public Prosjektdeltagelse(Ansatt ansatt, Prosjekt prosjekt, String rolle, int timer) {
        this.ansatt = ansatt;
        this.prosjekt = prosjekt;
        this.rolle = rolle;
        this.timer = timer;
        this.id.setAnsattId(ansatt.getId());
        this.id.setProsjektId(prosjekt.getId());
    }

    public ProsjektdeltagelseId getId() {
        return id;
    }

    public Ansatt getAnsatt() {
        return ansatt;
    }

    public void setAnsatt(Ansatt ansatt) {
        this.ansatt = ansatt;
        this.id.setAnsattId(ansatt.getId());
    }

    public Prosjekt getProsjekt() {
        return prosjekt;
    }

    public void setProsjekt(Prosjekt prosjekt) {
        this.prosjekt = prosjekt;
        this.id.setProsjektId(prosjekt.getId());
    }

    public String getRolle() {
        return rolle;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return String.format("%s %s - %s - %d timer",
                ansatt.getFornavn(), ansatt.getEtternavn(), rolle, timer);
    }
}