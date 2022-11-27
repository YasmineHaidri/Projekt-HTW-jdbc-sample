package de.htw_berlin.imi.db.entities;
import java.sql.Date;

/**
 * Base class for all rooms.
 */
public class Student extends Entity {

    private long matrikelNr;
    private String name;
    private String vorname;
    private Date geburtsdatum;
    private String geburtsort;
    private int semester;
    private String studienbeginn;

    public Student(final long id) {
        super(id);
    }

    public Student(final long id, long matrikelNr) {
        super(id);
        setMatrikelNr(matrikelNr);
    }

    public void setMatrikelNr(long matrikelNr) { this.matrikelNr = matrikelNr; }

    public long getMatrikelNr() { return matrikelNr; }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }


    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

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
