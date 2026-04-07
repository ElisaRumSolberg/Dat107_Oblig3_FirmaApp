package no.hvl.firmaapp;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProsjektdeltagelseId implements Serializable {

    private int ansattId;
    private int prosjektId;

    public ProsjektdeltagelseId() {
    }

    public ProsjektdeltagelseId(int ansattId, int prosjektId) {
        this.ansattId = ansattId;
        this.prosjektId = prosjektId;
    }

    public int getAnsattId() {
        return ansattId;
    }

    public void setAnsattId(int ansattId) {
        this.ansattId = ansattId;
    }

    public int getProsjektId() {
        return prosjektId;
    }

    public void setProsjektId(int prosjektId) {
        this.prosjektId = prosjektId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProsjektdeltagelseId that = (ProsjektdeltagelseId) o;
        return ansattId == that.ansattId && prosjektId == that.prosjektId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ansattId, prosjektId);
    }

    @Override
    public String toString() {
        return "(" + ansattId + ", " + prosjektId + ")";
    }
}