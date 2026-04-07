package no.hvl.firmaapp;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prosjekt")
public class Prosjekt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String navn;

    @Column(length = 255)
    private String beskrivelse;

    @OneToMany(
            mappedBy = "prosjekt",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Prosjektdeltagelse> deltagere = new ArrayList<>();

    public Prosjekt() {
    }

    public int getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public List<Prosjektdeltagelse> getDeltagere() {
        return deltagere;
    }

    public void leggTilDeltagelse(Prosjektdeltagelse pd) {
        deltagere.add(pd);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", id, navn, beskrivelse);
    }
}