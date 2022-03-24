package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
@Repository
public class SecurityServiceClass implements SecurityService{

    @PersistenceContext
    private EntityManager entityManager;

    //@Autowired
    //private ExperimentRepository experimentRepository;

    //@Autowired
    //private JournalRepository journalRepository;

    @Autowired
    private ResearcherService researcherService;

    @Autowired
    private ElementPermissionService elementPermissionService;


    /**
     * Return the Researcher that is logged in using the information in the SecurityContextHolder
     * @return Instance of the logged researcher.
     */
    @Override
    public Researcher getLoggedResearcher() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Optional<Researcher> researcher = this.researcherService.getAll().stream().filter(r -> r.getEmail().equals(username)).findFirst();

        // TODO: make error in case of null
        return researcher.orElse(new Researcher());
    }


    /**
     * Inspects the elementpermission table / class to search if there is a certain element of a certain desired type
     * that has a granted authority to the logged user bigger than the required one or not. Returns true if yes,
     * returns false if not.
     * @param authority Required level of authority.
     * @param uuid ID of an element
     * @param type Type of our element
     * @param <T> Parameter IEntity that conforms to the implementation of all of our elements.
     * @return True or false depending on if we can do the required operation on the element or not.
     */
    @Override
    public <T extends IEntity> boolean isResearcherAuthorized(Authority authority, UUID uuid, Class<T> type) {
        Researcher researcher = this.getLoggedResearcher();

        // Loop permissions table
        for (ElementPermission elementPermission : this.elementPermissionService.getAll())
        {
            // Select all permissions of the logged researcher that are pointing to an entity of type experiment and
            // that have an authority level below the required.
            if (elementPermission.getResearcher().equals(researcher)
                    && elementPermission.getType().equals(type)
                    && elementPermission.getAuthority().ordinal() >= authority.ordinal()
                    && elementPermission.getElement().getUUid().equals(uuid)
            )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Generic method that returns all the IEntity objects of certain type T from a researcher identified by its
     * username (email) with a greater authority than the requested: If we ask for read authority we will get all the
     * journals except the ones with NONE authority.
     * @param username email that identifies a researcher
     * @param authority Selected level of permissions of the elements we retrieve
     * @param type Explicit type of the elements we are retrieving. Can be Experiment or Journal.
     * @param <T> Generic type that extends an IEntity
     * @return List of elements that conform to the supplied desired characteristics.
     */
    @Override
    public <T extends IEntity> Set<T> getAuthorizedElement(String username, Authority authority, Class<T> type) {
        Set<T> result = new HashSet<>();
        Researcher researcher = this.getLoggedResearcher();

        // Loop permissions table
        for (ElementPermission elementPermission : this.elementPermissionService.getAll())
        {
            // Select all permissions of the logged researcher that are pointing to an entity of type experiment and
            // that have an authority level below the required.
            if (elementPermission.getResearcher().equals(researcher)
                    && elementPermission.getType().equals(type)
                    && elementPermission.getAuthority().ordinal() >= authority.ordinal())
            {
                result.add(elementPermission.getElement());
            }
        }

        return result;
    }


    /**
     * Get all the Journals that have an authorization level below or equal to the authorization required for the
     * logged user.
     * @param authority Level of authorization required
     * @return All the journal where the current user has more privileges assigned than the required ones.
     */
    @Override
    public Set<Journal> getAuthorizedJournal(Authority authority) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
        {
            return this.getAuthorizedElement(this.getLoggedResearcher().getEmail(), authority, Journal.class);
        }
        else
        {
            //TODO: ERRR
            return null;
        }
    }


    /**
     * Get all experiments that have an authorization level below or equal to the authorization required for the
     * logged user.
     * @param authority Level of authorization required.
     * @return All the experiment where the current user has more privileges assigned than the required ones.
     */
    @Override
    public Set<Experiment> getAuthorizedExperiment(Authority authority) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
        {
            return this.getAuthorizedElement(this.getLoggedResearcher().getEmail(), authority, Experiment.class);
        }
        else
        {
            //TODO: ERRR
            return null;
        }
    }

/*
    public List<Journal> getAuthorizedJournalQuery(Authority authority) {
        String queryStr = " SELECT DISTINCT j.uuid, j.description, j.name " +
                "FROM journal AS j, researcher, elementpermission AS e " +
                "WHERE e.researcher = " +
                "(SELECT researcher.uuid FROM researcher WHERE researcher.email = ?1) " +
                "AND e.journal_id = j.uuid " +
                "AND e.authority >= ?2";
        try {
            Query query = entityManager.createNativeQuery(queryStr, Journal.class);
            query.setParameter(1, ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            query.setParameter(2, authority.ordinal());

            List<Journal> res = query.getResultList();
            if (res == null)
            {
                LoggerFactory.getLogger(EChempadApplication.class).info("nulllpointerrr");
                return new LinkedList<>();

            }
            else
            {
                LoggerFactory.getLogger(EChempadApplication.class).info("noooonulllpointerrr");

                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
*/

}
