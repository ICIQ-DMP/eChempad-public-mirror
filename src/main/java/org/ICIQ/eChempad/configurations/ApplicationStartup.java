/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations;

import org.ICIQ.eChempad.entities.*;
import org.ICIQ.eChempad.repositories.*;
import org.ICIQ.eChempad.services.FileStorageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${signals.APIKeys.path}")
    private String APIKeysPath;

    @Value("${signals.APIKeys.filename}")
    private String APIKeyFilename;

    private final ResearcherRepository researcherRepository;

    private final JournalRepository journalRepository;

    private final ExperimentRepository experimentRepository;

    private final DocumentRepository documentRepository;

    private final ElementPermissionRepository elementPermissionRepository;

    private final RoleUserRepository roleUserRepository;

    private final FileStorageService fileStorageService;
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
    public ApplicationStartup(ResearcherRepository researcherRepository, JournalRepository journalRepository, ExperimentRepository experimentRepository, DocumentRepository documentRepository, ElementPermissionRepository elementPermissionRepository, RoleUserRepository roleUserRepository, FileStorageService fileStorageService) {
        this.researcherRepository = researcherRepository;
        this.journalRepository = journalRepository;
        this.experimentRepository = experimentRepository;
        this.documentRepository = documentRepository;
        this.elementPermissionRepository = elementPermissionRepository;
        this.roleUserRepository = roleUserRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        initializeDB();
    }

    String getAPIKey()
    {
        String out = "";

        if (this.APIKeysPath == null || this.APIKeyFilename == null)
        {
            return "";
        }
        else
        {
            // File path is passed as parameter
            File file = new File(this.APIKeysPath + "/" + this.APIKeyFilename);

            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (scanner.hasNextLine())
            {
                out += scanner.nextLine();
            }
        }
        return out;
    }

    private void initializeDB()
    {

        String APIKey = this.getAPIKey();
        // Researcher examples, implicitly are USERs
        Researcher elvisTech = new Researcher("Charles Good", "cgood@gmail.com", null, "password");
        Researcher aitorMenta = new Researcher("Aitor Menta", "mentolado@gmail.com", APIKey, "password");
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
        Experiment experimentCO2Reaction1 = new Experiment("Activation energy laser beam 500 W, refraction index 0.4", "Test 1 of the CO2 fixation with laser beams", CO2Reaction);
        Experiment experimentCO2Reaction2 = new Experiment("Activation energy laser beam 530 W, refraction index 0.4", "Test 2 of the CO2 fixation with laser beams",  CO2Reaction);
        Experiment experimentCO2Reaction3 = new Experiment("Activation energy laser beam 560 W, refraction index 0.4", "Test 3 of the CO2 fixation with laser beams", CO2Reaction);
        Experiment experimentCO2Reaction4 = new Experiment("Activation energy laser beam 500 W, refraction index 0.35", "Test 4 of the CO2 fixation with laser beams", CO2Reaction);

        // Experiments in ethanolProperties journal
        Experiment experimentEthanol1 = new Experiment("Ethanol properties 1", "Ethanol yield in Pd-Ni-P complexes 1 M media", ethanolProperties);
        Experiment experimentEthanol2 = new Experiment("Ethanol properties 2", "Ethanol yield in Pd-Ni-P complexes 0.5 M media", ethanolProperties);

        // Experiments in waterProperties journal
        Experiment experimentWater1 = new Experiment("Water properties 1", "Study of the effect of surface tension in the formation of micelles in water solutions", waterProperties);

        // Experiments in activationEnergy journal
        Experiment experimentActivationEnergy1 = new Experiment("Copper ligands activation energy ", "Study of the effect of surface tension in the formation of micelles in water solutions", activationEnergy);


        // Save experiments
        // Experiments in CO2Reaction journal
        this.experimentRepository.saveOrUpdate(experimentCO2Reaction1);
        this.experimentRepository.saveOrUpdate(experimentCO2Reaction2);
        this.experimentRepository.saveOrUpdate(experimentCO2Reaction3);
        this.experimentRepository.saveOrUpdate(experimentCO2Reaction4);

        // Experiments in ethanolProperties journal
        this.experimentRepository.saveOrUpdate(experimentEthanol1);
        this.experimentRepository.saveOrUpdate(experimentEthanol2);

        // Experiments in waterProperties journal
        this.experimentRepository.saveOrUpdate(experimentWater1);

        // Experiments in activationEnergy journal
        this.experimentRepository.saveOrUpdate(experimentActivationEnergy1);


        // Experiment permissions
        // CO2 reaction (Elvis Tech)
        ElementPermission experimentCO2Reaction1Permission = new ElementPermission(experimentCO2Reaction1, Authority.OWN, elvisTech);
        ElementPermission experimentCO2Reaction2Permission = new ElementPermission(experimentCO2Reaction2, Authority.OWN, elvisTech);
        ElementPermission experimentCO2Reaction3Permission = new ElementPermission(experimentCO2Reaction3, Authority.OWN, elvisTech);
        ElementPermission experimentCO2Reaction4Permission = new ElementPermission(experimentCO2Reaction4, Authority.OWN, elvisTech);

        // Ethanol properties (Aitor Menta)
        ElementPermission experimentEthanol1Permission = new ElementPermission(experimentEthanol1, Authority.OWN, aitorMenta);
        ElementPermission experimentEthanol2Permission = new ElementPermission(experimentEthanol2, Authority.OWN, aitorMenta);

        // Water properties (Elvis Tech)
        ElementPermission experimentWater1Permission = new ElementPermission(experimentWater1, Authority.OWN, elvisTech);

        // Activation Energies (Admin)
        ElementPermission experimentActivationEnergy1Permission= new ElementPermission(experimentActivationEnergy1, Authority.OWN, administrator);


        // Add permissions to the experiments
        // experiment CO2 permissions
        experimentCO2Reaction1.getPermissions().add(experimentCO2Reaction1Permission);
        experimentCO2Reaction2.getPermissions().add(experimentCO2Reaction2Permission);
        experimentCO2Reaction3.getPermissions().add(experimentCO2Reaction3Permission);
        experimentCO2Reaction4.getPermissions().add(experimentCO2Reaction4Permission);

        // Ethanol properties permissions
        experimentEthanol1.getPermissions().add(experimentEthanol1Permission);
        experimentEthanol2.getPermissions().add(experimentEthanol2Permission);

        // Water properties permissions
        experimentWater1.getPermissions().add(experimentWater1Permission);

        // Activation Energy permissions
        experimentActivationEnergy1.getPermissions().add(experimentActivationEnergy1Permission);


        // Save permissions of the experiments
        this.elementPermissionRepository.saveOrUpdate(experimentCO2Reaction1Permission);
        this.elementPermissionRepository.saveOrUpdate(experimentCO2Reaction2Permission);
        this.elementPermissionRepository.saveOrUpdate(experimentCO2Reaction3Permission);
        this.elementPermissionRepository.saveOrUpdate(experimentCO2Reaction4Permission);

        this.elementPermissionRepository.saveOrUpdate(experimentEthanol1Permission);
        this.elementPermissionRepository.saveOrUpdate(experimentEthanol2Permission);

        this.elementPermissionRepository.saveOrUpdate(experimentWater1Permission);

        this.elementPermissionRepository.saveOrUpdate(experimentActivationEnergy1Permission);


        // Document examples

        // Document License in experimentEthanol1 Experiment

        // Copy an arbitrary file as if it has been uploaded with the API
        FileStorageProperties fileStorageProperties = new FileStorageProperties();
        Path document1ExperimentEthanol1_license_path = Paths.get("/home/amarine/Desktop/eChempad/LICENSE.md");
        byte[] document1ExperimentEthanol1_license_content = null;
        try {
            document1ExperimentEthanol1_license_content = Files.readAllBytes(document1ExperimentEthanol1_license_path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        Document document1ExperimentEthanol1 = new Document("License", "Contains text that indicates the state of the copyright", experimentEthanol1);



        ElementPermission document1ExperimentEthanol1Permission = new ElementPermission(document1ExperimentEthanol1, Authority.OWN, aitorMenta);
        document1ExperimentEthanol1.getPermissions().add(document1ExperimentEthanol1Permission);
        document1ExperimentEthanol1 = this.documentRepository.saveOrUpdate(document1ExperimentEthanol1);
        this.elementPermissionRepository.saveOrUpdate(document1ExperimentEthanol1Permission);
        MultipartFile document1ExperimentEthanol1_license = new MockMultipartFile(document1ExperimentEthanol1.getUUid().toString(), document1ExperimentEthanol1.getUUid().toString(), "application/octet-stream", document1ExperimentEthanol1_license_content);

        document1ExperimentEthanol1.setPath(Paths.get(this.fileStorageService.storeFile(document1ExperimentEthanol1_license, document1ExperimentEthanol1.getUUid())).toString());


        this.documentRepository.saveOrUpdate(document1ExperimentEthanol1);




        // Documents photo in experimentEthanol2 Experiment
        // Copy an arbitrary file as if it has been uploaded with the API
        Path document2ExperimentEthanol1_photo_path = Paths.get("/home/amarine/Desktop/eChempad/src/main/resources/CA_certificates/cacerts");
        byte[] document2ExperimentEthanol1_photo_content = null;
        try {
            document2ExperimentEthanol1_photo_content = Files.readAllBytes(document2ExperimentEthanol1_photo_path);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        Document document2ExperimentEthanol1 = new Document("Photo", "Example photo of springboot", experimentEthanol1);


        ElementPermission document2ExperimentEthanol1Permission = new ElementPermission(document2ExperimentEthanol1, Authority.OWN, aitorMenta);
        document2ExperimentEthanol1.getPermissions().add(document2ExperimentEthanol1Permission);
        document2ExperimentEthanol1 = this.documentRepository.saveOrUpdate(document2ExperimentEthanol1);

        MultipartFile document2ExperimentEthanol1_photo = new MockMultipartFile(document2ExperimentEthanol1.getUUid().toString(), document2ExperimentEthanol1.getUUid().toString(), "application/octet-stream", document2ExperimentEthanol1_photo_content);


        this.elementPermissionRepository.saveOrUpdate(document2ExperimentEthanol1Permission);

        document2ExperimentEthanol1.setPath(Paths.get(this.fileStorageService.storeFile(document2ExperimentEthanol1_photo, document2ExperimentEthanol1.getUUid())).toString());

        this.documentRepository.saveOrUpdate(document2ExperimentEthanol1);

    }

}
