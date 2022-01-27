package org.ICIQ.eChempad.configurations;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.*;
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

    @Autowired
    private ExperimentRepositoryClass experimentRepositoryClass;

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
        Researcher elvisTech = new Researcher("Elvis Tech", "elvis.not.dead@tech.es", null);
        Researcher aitorMenta = new Researcher("Aitor Menta", "mentolado@gmail.com", null);

        Journal activationEnergy = new Journal("Comparation of the activation energy of reactions catalyzed by enzymes with copper ligands", "In these experiments we are trying to obtain experimentally the difference between the activation energy of a human cupredoxin when it is attached to its copper ligands");
        Journal waterProperties = new Journal("Water Properties", "Experiments that take advantage of the special physical properties of the H2O molecule, regarding its H2 bonds.");
        Journal ethanolProperties = new Journal("Ethanol properties", "Set of reaction that use ethanol as reactive or media to improve yield");
        Journal CO2Reaction = new Journal("CO2 reduction to HCO3- using laser beams as an initial alternative approach to CO2 fixation to fight global warming", "In these set of experiments we are trying different approaches and parameters to activate CO2 using a high-energy laser beam, which will allow us to ionize our CO2 molecule, and transform to other carbon forms that produce less global warmign effect.");

        Experiment experimentActivation1 = new Experiment("Activation energy laser beam 500 W, refraction index 0.4", "Test 1 of the CO2 fixation with laser beams", CO2Reaction);
        Experiment experimentActivation2 = new Experiment("Activation energy laser beam 530 W, refraction index 0.4", "Test 2 of the CO2 fixation with laser beams",  CO2Reaction);
        Experiment experimentActivation3 = new Experiment("Activation energy laser beam 560 W, refraction index 0.4", "Test 3 of the CO2 fixation with laser beams", CO2Reaction);
        Experiment experimentActivation4 = new Experiment("Activation energy laser beam 500 W, refraction index 0.35", "Test 4 of the CO2 fixation with laser beams", CO2Reaction);

        Experiment experimentEthanol1 = new Experiment("Ethanol properties 1", "Ethanol yield in Pd-Ni-P complexes 1 M media", ethanolProperties);
        Experiment experimentEthanol2 = new Experiment("Ethanol properties 2", "Ethanol yield in Pd-Ni-P complexes 0.5 M media", ethanolProperties);

        Experiment experimentWater1 = new Experiment("Water properties 1", "Study of the effect of surface tension in the formation of micelles in water solutions", waterProperties);

        Experiment activationEnergy1 = new Experiment("Copper ligands activation energy ", "Study of the effect of surface tension in the formation of micelles in water solutions", waterProperties);


        this.researcherRepositoryClass.saveOrUpdate(elvisTech);
        this.researcherRepositoryClass.saveOrUpdate(aitorMenta);

        this.journalRepositoryClass.saveOrUpdate(activationEnergy);
        this.journalRepositoryClass.saveOrUpdate(waterProperties);
        this.journalRepositoryClass.saveOrUpdate(ethanolProperties);
        this.journalRepositoryClass.saveOrUpdate(CO2Reaction);

        this.experimentRepositoryClass.saveOrUpdate(experimentActivation1);
        this.experimentRepositoryClass.saveOrUpdate(experimentActivation2);
        this.experimentRepositoryClass.saveOrUpdate(experimentActivation3);
        this.experimentRepositoryClass.saveOrUpdate(experimentActivation4);

        this.experimentRepositoryClass.saveOrUpdate(experimentEthanol1);
        this.experimentRepositoryClass.saveOrUpdate(experimentEthanol2);

        this.experimentRepositoryClass.saveOrUpdate(experimentWater1);

        this.experimentRepositoryClass.saveOrUpdate(activationEnergy1);
    }

}
