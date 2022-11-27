package de.htw_berlin.imi.db.services;

import de.htw_berlin.imi.db.entities.BueroRaum;
import de.htw_berlin.imi.db.entities.Professor;
import de.htw_berlin.imi.db.web.BueroDto;
import de.htw_berlin.imi.db.web.ProfessorDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class ProfessorEntityServiceTest {
    @Autowired
    ProfessorEntityService profEntityService;

    @Test
    void findAll() {
        final List<Professor> all = profEntityService.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all)
                .extracting(Professor::getId)
                .isNotEmpty();
    }

    @Test
    void findById() {
        final Optional<Professor> profOptional = profEntityService.findById(2125);
        assertThat(profOptional).isPresent();
        assertThat(profOptional.get().getName()).isEqualTo("Sokrates");
    }

    @Test
    void getProfessorWithHisBuero() {
        final Optional<Professor> profOptional = profEntityService.findById(2126);
        assertThat(profOptional).isPresent();
        final BueroRaum buro = profOptional.get().getRaum();
        assertThat(buro.getName())
                .contains("Office#2");
    }

    @Test
    void cannotfindById() {
        final Optional<Professor> profOptional = profEntityService.findById(0);
        assertThat(profOptional).isNotPresent();
    }

    @Test
    void createNew() {
        final Professor prof = profEntityService.create();
        assertThat(prof).isNotNull();
        assertThat(prof.getId()).isGreaterThan(1000);
        final Professor prof1 = profEntityService.create();
        assertThat(prof.getId()).isLessThan(prof1.getId());
    }

    @Test
    void save() {
        final Professor prof = profEntityService.create();
        prof.setName("Alkhawarezmi");
        prof.setGehalt(70000);
        prof.setRang("C3");
        BueroRaum buero= new BueroRaum(11);
        prof.setRaum(buero);

        profEntityService.save(prof);

        final Optional<Professor> profOptional = profEntityService.findById(prof.getId());
        assertThat(profOptional).isPresent();
        assertThat(profOptional.get().getGehalt()).isEqualTo(70000);
        assertThat(profOptional.get().getName()).isEqualTo("Alkhawarezmi");
        assertThat(profOptional.get().getRang()).isEqualTo("C3");
    }

    @Test
    void delete() {
        final Professor prof = profEntityService.create();
        prof.setName("Alkhawarezmi");
        prof.setGehalt(70000);
        prof.setRang("C3");
        BueroRaum buro= new BueroRaum(7);
        prof.setRaum(buro);
        profEntityService.save(prof);
        profEntityService.delete(prof);
        final Optional<Professor> profOptional = profEntityService.findById(prof.getId());
        assertThat(profOptional).isNotPresent();
    }

    @Test
    void createFrom() {
        final ProfessorDto profDto = new ProfessorDto();
        profDto.setName("Alkhawarezmi");
        profDto.setGehalt(70000);
        profDto.setRang("C3");

        final Professor from = profEntityService.createFrom(profDto);

        final Optional<Professor> profOptional = profEntityService.findById(from.getId());
        assertThat(profOptional).isPresent();
        assertThat(profOptional.get().getName()).isEqualTo("Alkhawarezmi");
        assertThat(profOptional.get().getGehalt()).isEqualTo(70000);
        assertThat(profOptional.get().getRang()).isEqualTo("C3");
    }

    @Test
    void update() {
        final Optional<Professor> profOptional = profEntityService.findById(2127);
        profOptional.ifPresent(p -> {
            p.setRang("C4");
            profEntityService.update(p);
        });
        final Optional<Professor> updatedProf = profEntityService.findById(2127);
        assertThat(updatedProf).isPresent();
        assertThat(updatedProf.get().getRang()).isEqualTo("C4");
    }
}
