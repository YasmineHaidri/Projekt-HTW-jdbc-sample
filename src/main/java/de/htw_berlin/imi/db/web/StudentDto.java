package de.htw_berlin.imi.db.web;
import java.sql.Date;

public class StudentDto {
    private String name;
    private String vorname;
    private Date geburtsdatum;
    private String geburtsort;
    private int semester;
    private String studienbeginn;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public Date getGeburtsdatum() { return geburtsdatum; }

    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getGeburtsort() {
        return geburtsort;
    }

    public void setGeburtsort(String geburtsort) {
        this.geburtsort = geburtsort;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getStudienbeginn() {
        return studienbeginn;
    }

    public void setStudienbeginn(String studienbeginn) {
        this.studienbeginn = studienbeginn;
    }
}
