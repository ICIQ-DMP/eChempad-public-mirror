/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.database;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.ICIQ.eChempad.services.genericJPAServices.ResearcherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class contains the method
 * {@code public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)}
 * which is executed after the application is "ready". That happens after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start of the application.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 10/10/2022
 */
@Component
public class DatabaseInitStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * We use our {@code ResearcherService} to load users to check the database state in order to know if we need to
     * create a basic admin user.
     */
    @Autowired
    private ResearcherService<Researcher, UUID> researcherService;

    /**
     * We use our {@code JournalService} to create new example journals.
     */
    @Autowired
    private ContainerService<Container, UUID> containerService;

    /**
     * This code run after the application is completely ready but just before it starts serving requests.
     * @param event Data associated with the event of the application being ready.
     */
    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        this.initializeDB();
    }

    /**
     * Initializes the database with basic data.
     */
    private void initializeDB()
    {
        Logger.getGlobal().info("INITIALIZING DB");
        this.initAdminResearcher();
        Logger.getGlobal().info("END INITIALIZING DB");
    }

    private String getSignalsAPIKey()
    {
        StringBuilder data = new StringBuilder();
        try {
            File myObj = new File("src/main/resources/secrets/signalsKey.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data.toString();
    }

    private String getDataverseAPIKey()
    {
        StringBuilder data = new StringBuilder();
        try {
            File myObj = new File("src/main/resources/secrets/dataverseKey.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data.toString();
    }

    /**
     * Initializes an admin researcher with data.
     */
    private void initAdminResearcher() {

        // If we have the administrator already in the DB skip initialization
        if (this.researcherService.loadUserByUsername("eChempad@iciq.es") != null)
            return;

        // Init the admin user
        Researcher researcher = new Researcher();

        researcher.setSignalsAPIKey(this.getSignalsAPIKey());
        researcher.setSignalsAPIKey(this.getDataverseAPIKey());

        researcher.setAccountNonExpired(true);
        researcher.setEnabled(true);
        researcher.setCredentialsNonExpired(true);
        researcher.setPassword("chemistry");
        researcher.setUsername("eChempad@iciq.es");
        researcher.setAccountNonLocked(true);
        researcher.setCreationDate(new Date());

        HashSet<Authority> authorities = new HashSet<>();
        authorities.add(new Authority("ROLE_ADMIN", researcher));
        authorities.add(new Authority("ROLE_USER", researcher));
        researcher.setAuthorities(authorities);

        // If the user is not in the DB create it
        // Authenticate user, or we will not be able to manipulate the ACL service with the security context empty
        Authentication auth = new UsernamePasswordAuthenticationToken(researcher.getUsername(), researcher.getPassword(), researcher.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Save of the authority is cascaded
        this.researcherService.save(researcher);
        
        // Now create some data associated with this admin user
        //this.initJournal();
    }

    /**
     * Initializes a test journal.
     */
    private void initJournal() {
        Container container = new Container();

        container.setDescription("The journal of the admin");
        container.setName("Journal Admin");
        container.initCreationDate();

        // Indirectly obtain the ID of the journal by saving it on the DB
        this.containerService.save(container);
    }

    // Constructors

    public DatabaseInitStartup() {}
}
