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
import java.util.*;
import java.util.logging.Logger;

/**
 * This class contains the method
 * {@code public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)}
 * which is executed after the application is "ready". That happens after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start of the application
 * programmatically.
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
     * Class Logger
     */
    private final static Logger LOGGER = Logger.getLogger(DatabaseInitStartup.class.getName());

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
        DatabaseInitStartup.LOGGER.info("INITIALIZING DB");
        this.initAdminResearcher();
        DatabaseInitStartup.LOGGER.info("END INITIALIZING DB");
    }

    /**
     * Retrieves a key if available. If not returns null. First tries reading the file keyName.txt in the secrets
     * folder to obtain the key. If the file does not exist it tries reading from environment variable with the name
     * keyName.
     *
     * @return String containing the desired key. Null if not provided both from file and environment variable.
     */
    private String getKey(String keyName)
    {
        StringBuilder data = new StringBuilder();
        try {
            File myObj = new File("target/classes/secrets/" + keyName + ".txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            DatabaseInitStartup.LOGGER.info(
                    "file " + keyName + " not found, " +
                    "trying to obtain " + keyName + " from env.");
            // The file of the key does not exist, try from env variables
            String value = System.getenv(keyName);
            data.append(value);
        }
        return data.toString();
    }

    /**
     * Tries to obtain the signals API key. First, tries to obtain it from file by reading the file
     * secrets/SIGNALS_KEY.txt . If it does not exist, it tries reading the signals key from the environment from the
     * variable SIGNALS_KEY. If it does not exist too, returns null.
     *
     * @return API key from signals if available from any source, null otherwise.
     */
    private String getSignalsAPIKey()
    {
        return getKey("SIGNALS_KEY");
    }

    /**
     * Tries to obtain the dataverse API key. First, tries to obtain it from file by reading the file
     * secrets/DATAVERSE_KEY.txt . If it does not exist, it tries reading the dataverse key from the environment from
     * the variable DATAVERSE_KEY. If it does not exist too, returns null.
     *
     * @return  API key from dataverse if available from any source, null otherwise.
     */
    private String getDataverseAPIKey()
    {
        return getKey("DATAVERSE_KEY");
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
        researcher.setDataverseAPIKey(this.getDataverseAPIKey());

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
