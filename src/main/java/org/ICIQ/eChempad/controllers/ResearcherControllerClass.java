package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.services.ResearcherService;
import org.ICIQ.eChempad.services.ResearcherServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.HashPrintJobAttributeSet;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/researcher")
public class ResearcherControllerClass implements ResearcherController {

    @Autowired
    private ResearcherServiceClass researcherServiceClass;

    public ResearcherControllerClass(ResearcherServiceClass researcherServiceClass) {
        this.researcherServiceClass = researcherServiceClass;
    }


    @Override
    @GetMapping
    public Set<Researcher> getAllResearchers() {
        return new HashSet<>(this.researcherServiceClass.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Researcher> getResearcher(@PathVariable(value = "id") UUID uuid) {
        Researcher researcher = this.researcherServiceClass.get(uuid);

        if (researcher != null)
        {
            return ResponseEntity.ok().body(researcher);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public Researcher addResearcher(@Validated @RequestBody Researcher researcher) {
        return this.researcherServiceClass.saveOrUpdate(researcher);
    }
}
