package no.hvl.firmaapp;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ansatt")
public class Ansatt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 4)
    private String brukernavn;

    @Column(nullable = false, length = 50)
    private String fornavn;

    @Column(nullable = false, length = 50)
    private String etternavn;

    @Column(nullable = false)
    private LocalDate ansettelsesdato;

    @Column(nullable = false, length = 50)
    private String stilling;

    @Column(name = "maanedsloen", nullable = false)
    private double maanedsloen;

    public Ansatt() {
    }

    public int getId() {
        return id;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public void setBrukernavn(String brukernavn) {
        this.brukernavn = brukernavn;
    }

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public LocalDate getAnsettelsesdato() {
        return ansettelsesdato;
    }

    public void setAnsettelsesdato(LocalDate ansettelsesdato) {
        this.ansettelsesdato = ansettelsesdato;
    }

    public String getStilling() {
        return stilling;
    }

    public void setStilling(String stilling) {
        this.stilling = stilling;
    }

    public double getMaanedsloen() {
        return maanedsloen;
    }

    public void setMaanedsloen(double maanedsloen) {
        this.maanedsloen = maanedsloen;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s (%s) - %s - %.0f kr",
                id, fornavn, etternavn, brukernavn, stilling, maanedsloen);
    }
}