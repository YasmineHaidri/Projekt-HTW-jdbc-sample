package de.htw_berlin.imi.db.services;

import de.htw_berlin.imi.db.entities.BueroRaum;
import de.htw_berlin.imi.db.entities.Stockwerk;
import de.htw_berlin.imi.db.entities.Student;
import de.htw_berlin.imi.db.web.BueroDto;
import de.htw_berlin.imi.db.web.StudentDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StudentEntityServiceTest {

    @Autowired
    StudentEntityService studentEntityService;

    @Test
    void findAll() {
        final List<Student> all = studentEntityService.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all).extracting(Student::getId).isNotEmpty();
    }

    @Test
    void findById() {
        final Optional<Student> studentOptional = studentEntityService.findById(1);
        assertThat(studentOptional).isPresent();
        assertThat(studentOptional.get().getName()).isEqualTo("Xenokrates");
    }

    @Test
    void cannotfindById() {
        final Optional<Student> studentOptional = studentEntityService.findById(0);
        assertThat(studentOptional).isNotPresent();
    }

    @Test
    void createNew() {
        final Student student = studentEntityService.create();
        assertThat(student).isNotNull();
        assertThat(student.getId()).isPositive();

        final Student student2 = studentEntityService.create();
        assertThat(student.getId()).isLessThan(student2.getId());
    }

    @Test
    void save() {
        final Student student = studentEntityService.create();
        student.setName("Max");
        student.setVorname("Muster");
        Date geburtstag = new Date(new GregorianCalendar(1990, 0, 1).getTime().getTime());
        student.setGeburtsdatum(geburtstag);
        student.setGeburtsort("Musterstadt");
        student.setSemester(1);
        student.setStudienbeginn("SS 2022");

        studentEntityService.save(student);

        final Optional<Student> studentOptional = studentEntityService.findById(student.getId());
        assertThat(studentOptional).isPresent();
        assertThat(studentOptional.get().getMatrikelNr()).isPositive();
        assertThat(studentOptional.get().getName()).isEqualTo("Max");
        assertThat(studentOptional.get().getVorname()).isEqualTo("Muster");
        assertThat(studentOptional.get().getGeburtsdatum()).isEqualTo(geburtstag);
        assertThat(studentOptional.get().getGeburtsort()).isEqualTo("Musterstadt");
        assertThat(studentOptional.get().getSemester()).isEqualTo(1);
        assertThat(studentOptional.get().getStudienbeginn()).isEqualTo("SS 2022");
    }

    @Test
    void delete() {
        final Student student = studentEntityService.create();
        // Wir brauchen valide Daten, um den Studenten erst speichern zu können
        student.setName("Max");
        student.setVorname("Muster");
        student.setGeburtsdatum(new Date(new GregorianCalendar(1990, 0, 1).getTime().getTime()));
        student.setGeburtsort("Musterstadt");
        student.setSemester(1);
        student.setStudienbeginn("SS 2022");

        long id = student.getId();
        studentEntityService.save(student);
        studentEntityService.delete(student);

        final Optional<Student> studentResult = studentEntityService.findById(id);
        assertThat(studentResult).isNotPresent();
    }

    @Test
    void createFrom() {
        final StudentDto student = new StudentDto();
        student.setName("Max");
        student.setVorname("Muster");
        Date geburtstag = new Date(new GregorianCalendar(1990, 0, 1).getTime().getTime());
        student.setGeburtsdatum(geburtstag);
        student.setGeburtsort("Musterstadt");
        student.setSemester(1);
        student.setStudienbeginn("SS 2022");

        final Student from = studentEntityService.createFrom(student);

        final Optional<Student> studentOptional = studentEntityService.findById(from.getId());
        assertThat(studentOptional).isPresent();
        assertThat(studentOptional.get().getMatrikelNr()).isPositive();
        assertThat(studentOptional.get().getName()).isEqualTo("Max");
        assertThat(studentOptional.get().getVorname()).isEqualTo("Muster");
        assertThat(studentOptional.get().getGeburtsdatum()).isEqualTo(geburtstag);
        assertThat(studentOptional.get().getGeburtsort()).isEqualTo("Musterstadt");
        assertThat(studentOptional.get().getSemester()).isEqualTo(1);
        assertThat(studentOptional.get().getStudienbeginn()).isEqualTo("SS 2022");
    }

    @Test
    void update() {
        final Optional<Student> student = studentEntityService.findById(1);
        student.ifPresent(s -> {
            s.setGeburtsort("Berlin");
            studentEntityService.update(s);
        });

        final Optional<Student> studentResult = studentEntityService.findById(1);
        assertThat(studentResult).isPresent();
        assertThat(studentResult.get().getGeburtsort()).isEqualTo("Berlin");
    }
}