package de.htw_berlin.imi.db.web;

import de.htw_berlin.imi.db.entities.Professor;
import de.htw_berlin.imi.db.services.ProfessorEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller()
@RequestMapping(path = "/ui/professoren")
public class ProfessorenMvcController {

    @Autowired
    ProfessorEntityService professorEntityService;

    @GetMapping
    String findAll(final Model model) {
        model.addAttribute("professoren", professorEntityService.findAll());
        // empty template object that accepts fiel values from
        // the HTML form when new office room objetcs are created
        model.addAttribute("professorTemplate", new ProfessorDto());
        return "professoren";
    }

    @GetMapping("/{id}")
    String find(final Model model,
                @PathVariable("id") final long id) {
        model.addAttribute("professor",
                professorEntityService
                        .findById(id)
                        .orElseThrow(IllegalArgumentException::new));
        return "professor-detail";
    }

    @PostMapping("")
    String createProf(@ModelAttribute("professorTemplate") final ProfessorDto professorTemplate) {
        professorEntityService.createFrom(professorTemplate);
        // causes a page reload
        return "redirect:/ui/professoren";
    }

    @DeleteMapping("/{id}")
    String deleteProf(@PathVariable("id") final long id) {
        professorEntityService
                .findById(id)
                .ifPresent(b -> professorEntityService.delete(b));
        // causes a page reload
        return "redirect:/ui/professoren";
    }

}
