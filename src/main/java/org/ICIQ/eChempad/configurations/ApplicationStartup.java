package org.ICIQ.eChempad.configurations;

import org.ICIQ.eChempad.entities.*;
import org.ICIQ.eChempad.repositories.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.Path;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final ResearcherRepository researcherRepository;

    private final JournalRepository journalRepository;

    private final ExperimentRepository experimentRepository;

    private final DocumentRepository documentRepository;

    private final ElementPermissionRepository elementPermissionRepository;

    private final RoleUserRepository roleUserRepository;

    /**
     * Using classic constructor since when using autowired injection in the fields is discouraged because breaks
     * encapsulation and very random behaviour was happening.
     * https://www.baeldung.com/constructor-injection-in-spring
     * @param researcherRepository
     * @param journalRepository
     * @param experimentRepository
     * @param documentRepository
     * @param elementPermissionRepository
     * @param roleUserRepository
     */
    @Autowired
    public ApplicationStartup(ResearcherRepository researcherRepository, JournalRepository journalRepository, ExperimentRepository experimentRepository, DocumentRepository documentRepository, ElementPermissionRepository elementPermissionRepository, RoleUserRepository roleUserRepository) {
        this.researcherRepository = researcherRepository;
        this.journalRepository = journalRepository;
        this.experimentRepository = experimentRepository;
        this.documentRepository = documentRepository;
        this.elementPermissionRepository = elementPermissionRepository;
        this.roleUserRepository = roleUserRepository;
    }

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        initializeDB();
    }

    private void initializeDB()
    {
        // Researcher examples, implicitly are USERs
        Researcher elvisTech = new Researcher("Elvis Tech", "elvis.not.dead@tech.es", null, "password");
        Researcher aitorMenta = new Researcher("Aitor Menta", "mentolado@gmail.com", null, "password");
        Researcher administrator = new Researcher("Administrator", "admin@eChempad.com", null, "password");

        RoleUser elvisTechRole = new RoleUser(elvisTech, Role.USER);
        RoleUser aitorMentaRole = new RoleUser(aitorMenta, Role.USER);
        RoleUser administratorRole = new RoleUser(administrator, Role.USER);
        RoleUser administratorRoleAdmin = new RoleUser(administrator, Role.ADMIN);

        this.researcherRepository.saveOrUpdate(elvisTech);
        this.researcherRepository.saveOrUpdate(aitorMenta);
        this.researcherRepository.saveOrUpdate(administrator);

        this.roleUserRepository.saveOrUpdate(elvisTechRole);
        this.roleUserRepository.saveOrUpdate(aitorMentaRole);
        this.roleUserRepository.saveOrUpdate(administratorRole);
        this.roleUserRepository.saveOrUpdate(administratorRoleAdmin);

        // Journal examples
        Journal activationEnergy = new Journal("Comparation of the activation energy of reactions catalyzed by enzymes with copper ligands", "In these experiments we are trying to obtain experimentally the difference between the activation energy of a human cupredoxin when it is attached to its copper ligands");
        Journal waterProperties = new Journal("Water Properties", "Experiments that take advantage of the special physical properties of the H2O molecule, regarding its H2 bonds.");
        Journal ethanolProperties = new Journal("Ethanol properties", "Set of reaction that use ethanol as reactive or media to improve yield");
        Journal CO2Reaction = new Journal("CO2 reduction to HCO3- using laser beams as an initial alternative approach to CO2 fixation to fight global warming", "In these set of experiments we are trying different approaches and parameters to activate CO2 using a high-energy laser beam, which will allow us to ionize our CO2 molecule, and transform to other carbon forms that produce less global warmign effect.");

        // Journal permissions
        ElementPermission activationEnergyPermission = new ElementPermission(activationEnergy, Authority.OWN, elvisTech);
        ElementPermission waterPropertiesPermission = new ElementPermission(waterProperties, Authority.EDIT, elvisTech);
        ElementPermission ethanolPropertiesPermission = new ElementPermission(ethanolProperties, Authority.WRITE, aitorMenta);
        ElementPermission CO2ReactionPermission= new ElementPermission(CO2Reaction, Authority.READ, administrator);

        // Add permissions to the journals
        activationEnergy.getPermissions().add(activationEnergyPermission);
        waterProperties.getPermissions().add(waterPropertiesPermission);
        ethanolProperties.getPermissions().add(ethanolPropertiesPermission);
        CO2Reaction.getPermissions().add(CO2ReactionPermission);


        this.journalRepository.saveOrUpdate(activationEnergy);
        this.journalRepository.saveOrUpdate(waterProperties);
        this.journalRepository.saveOrUpdate(ethanolProperties);
        this.journalRepository.saveOrUpdate(CO2Reaction);


        this.elementPermissionRepository.saveOrUpdate(activationEnergyPermission);
        this.elementPermissionRepository.saveOrUpdate(waterPropertiesPermission);
        this.elementPermissionRepository.saveOrUpdate(ethanolPropertiesPermission);
        this.elementPermissionRepository.saveOrUpdate(CO2ReactionPermission);


        // Experiment examples
        // Experiments in CO2Reaction journal
        Experiment experimentActivation1 = new Experiment("Activation energy laser beam 500 W, refraction index 0.4", "Test 1 of the CO2 fixation with laser beams", CO2Reaction);
        Experiment experimentActivation2 = new Experiment("Activation energy laser beam 530 W, refraction index 0.4", "Test 2 of the CO2 fixation with laser beams",  CO2Reaction);
        Experiment experimentActivation3 = new Experiment("Activation energy laser beam 560 W, refraction index 0.4", "Test 3 of the CO2 fixation with laser beams", CO2Reaction);
        Experiment experimentActivation4 = new Experiment("Activation energy laser beam 500 W, refraction index 0.35", "Test 4 of the CO2 fixation with laser beams", CO2Reaction);
        this.experimentRepository.saveOrUpdate(experimentActivation1);
        this.experimentRepository.saveOrUpdate(experimentActivation2);
        this.experimentRepository.saveOrUpdate(experimentActivation3);
        this.experimentRepository.saveOrUpdate(experimentActivation4);

        // Experiments in ethanolProperties journal
        Experiment experimentEthanol1 = new Experiment("Ethanol properties 1", "Ethanol yield in Pd-Ni-P complexes 1 M media", ethanolProperties);
        Experiment experimentEthanol2 = new Experiment("Ethanol properties 2", "Ethanol yield in Pd-Ni-P complexes 0.5 M media", ethanolProperties);
        this.experimentRepository.saveOrUpdate(experimentEthanol1);
        this.experimentRepository.saveOrUpdate(experimentEthanol2);

        // Experiments in waterProperties journal
        Experiment experimentWater1 = new Experiment("Water properties 1", "Study of the effect of surface tension in the formation of micelles in water solutions", waterProperties);
        this.experimentRepository.saveOrUpdate(experimentWater1);

        // Experiments in activationEnergy journal
        Experiment activationEnergy1 = new Experiment("Copper ligands activation energy ", "Study of the effect of surface tension in the formation of micelles in water solutions", waterProperties);
        this.experimentRepository.saveOrUpdate(activationEnergy1);


        // Document examples

        Path license = FileSystems.getDefault().getPath("/home/amarine/Desktop/eChempad/COPYING");
        // Documents in experimentEthanol1 Experiment
        Document documentEthanolTheory = new Document("License", "Contains text that indicates the state of the copyright", license, experimentEthanol1);
        this.documentRepository.saveOrUpdate(documentEthanolTheory);


        Path binary = FileSystems.getDefault().getPath("/home/amarine/Downloads/foto.webp");
        // Documents in experimentEthanol1 Experiment
        Document documentEthanolTheory_binary = new Document("Photo", "Example photo of springboot", binary, experimentEthanol1);
        this.documentRepository.saveOrUpdate(documentEthanolTheory_binary);

    }

}
