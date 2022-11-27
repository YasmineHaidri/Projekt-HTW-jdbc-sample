package de.htw_berlin.imi.db.web;

import de.htw_berlin.imi.db.services.StudentEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller()
@RequestMapping(path = "/ui/studierende")
public class StudentMvcController {

    @Autowired
    StudentEntityService studentEntityService;

    @GetMapping
    String findAll(final Model model) {
        model.addAttribute("studierende", studentEntityService.findAll());
        // empty template object that accepts fiel values from
        // the HTML form when new office room objetcs are created
        model.addAttribute("studentTemplate", new StudentDto());
        return "studierende";
    }

    @GetMapping("/{id}")
    String find(final Model model,
                @PathVariable("id") final long id) {
        model.addAttribute("student",
                studentEntityService
                        .findById(id)
                        .orElseThrow(IllegalArgumentException::new));
        return "student-details";
    }

    @PostMapping("")
    String createStudent(@ModelAttribute("studentTemplate") final StudentDto studentTemplate) {
        studentEntityService.createFrom(studentTemplate);
        // causes a page reload
        return "redirect:/ui/studierende";
    }

    @DeleteMapping("/{id}")
    String deleteStudent(@PathVariable("id") final long id) {
        studentEntityService
                .findById(id)
                .ifPresent(b -> studentEntityService.delete(b));
        // causes a page reload
        return "redirect:/ui/studierende";
    }
}
