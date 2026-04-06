package no.hvl.firmaapp;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "avdeling")
public class Avdeling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String navn;

    // Şef
    @ManyToOne
    @JoinColumn(name = "sjef_id")
    private Ansatt sjef;

    // Avdeling içindeki çalışanlar
    @OneToMany(mappedBy = "avdeling")
    private List<Ansatt> ansatte;

    public Avdeling() {
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


    public Ansatt getSjef() {
        return sjef;
    }

    public void setSjef(Ansatt sjef) {
        this.sjef = sjef;
    }

    public List<Ansatt> getAnsatte() {
        return ansatte;
    }

    @Override
    public String toString() {
        String sjefNavn = sjef != null
                ? sjef.getFornavn() + " " + sjef.getEtternavn()
                : "Ingen sjef";

        return String.format("[%d] %s (Sjef: %s)", id, navn, sjefNavn);
    }


}