package de.htw_berlin.imi.db.web;

import de.htw_berlin.imi.db.entities.BueroRaum;

public class ProfessorDto {
    private String name;

    private String rang;

    private int gehalt;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRang() {
        return rang;
    }

    public void setRang(final String rang) {
        this.rang = rang;
    }

    public int getGehalt() {
        return gehalt;
    }

    public void setGehalt(final int gehalt) {
        this.gehalt = gehalt;
    }

}
