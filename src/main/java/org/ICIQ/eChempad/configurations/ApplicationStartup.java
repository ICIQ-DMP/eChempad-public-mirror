package org.ICIQ.eChempad.configurations;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.ICIQ.eChempad.repositories.JournalRepositoryClass;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepositoryClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ResearcherRepositoryClass researcherRepositoryClass;

    @Autowired
    private JournalRepositoryClass journalRepositoryClass;



    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        initializeDB();
    }

    private void initializeDB()
    {
        this.researcherRepositoryClass.saveOrUpdate(new Researcher("Elvis Tech", "elvis.not.dead@tech.es", null));
        this.researcherRepositoryClass.saveOrUpdate(new Researcher("Aitor Menta", "mentolado@gmail.com", null));

        this.journalRepositoryClass.saveOrUpdate(new Journal("Water Properties", "Experiments that take advantage of the special physical properties of the H2O molecule, regarding its H2 bonds."));
        this.journalRepositoryClass.saveOrUpdate(new Journal("Ethanol properties", "Set of reaction that use ethanol as reactive or media to improve yield"));
        this.journalRepositoryClass.saveOrUpdate(new Journal("Comparation of the activation energy of reactions catalyzed by enzymes with copper ligands", "In these experiments we are trying to obtain experimentally the difference between the activation energy of a human cupredoxin when it is attached to its copper ligands"));
        this.journalRepositoryClass.saveOrUpdate(new Journal("CO2 reduction to HCO3- using laser beams as an initial alternative approach to CO2 fixation to fight global warming", "In these set of experiments we are trying different approaches and parameters to activate CO2 using a high-energy laser beam, which will allow us to ionize our CO2 molecule, and transform to other carbon forms that produce less global warmign effect."));
    }

}
