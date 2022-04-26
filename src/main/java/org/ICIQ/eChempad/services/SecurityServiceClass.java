/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.*;
import org.ICIQ.eChempad.repositories.DocumentRepository;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
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

    private final DocumentRepository documentRepository;

    private final ExperimentRepository experimentRepository;

    private final JournalRepository journalRepository;

    private final ResearcherService researcherService;

    private final ElementPermissionService elementPermissionService;

    public SecurityServiceClass(DocumentRepository documentRepository, ExperimentRepository experimentRepository, JournalRepository journalRepository, ResearcherService researcherService, ElementPermissionService elementPermissionService) {
        this.documentRepository = documentRepository;
        this.experimentRepository = experimentRepository;
        this.journalRepository = journalRepository;
        this.researcherService = researcherService;
        this.elementPermissionService = elementPermissionService;
    }


    @Override
    public Researcher getLoggedResearcher() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Optional<Researcher> researcher = this.researcherService.get().stream().filter(r -> r.getEmail().equals(username)).findFirst();

        // TODO: make error in case of null
        return researcher.orElse(new Researcher());
    }

    @Override
    public <T extends IEntity> boolean isResearcherAuthorized(Authority authority, UUID uuid, Class<T> type) {
        Researcher researcher = this.getLoggedResearcher();

        // Loop permissions table
        for (ElementPermission elementPermission : this.elementPermissionService.get())
        {
            /*
             * Depending on the type we need to ensure permissions in the upper part of the tree, for example, to check
             * if we have permission to read a document we need to check the permissions of the document, and if we do
             * not have an explicit permission, we should check the permissions of the experiment that is containing the
             * documents.
             */
            // Explicit permissions in the same level
            if (elementPermission.getResearcher().equals(researcher)
                    && elementPermission.getMyType().equals(type)
                    && elementPermission.getAuthority().ordinal() >= authority.ordinal()
                    && elementPermission.getElement().getUUid().equals(uuid) )
            {
                return true;
            }
            else  // We do not have permissions in the same level. We need to go to the parent if exists.
            {
                /*
                 * The current iterated permission refers to an element that is the container of the element that we are
                 * querying
                 */
                if (elementPermission.getElement().isContainer(uuid))
                {
                    // Return true if the container has permissions for the logged user
                    if (elementPermission.getResearcher().equals(researcher) && elementPermission.getAuthority().ordinal() >= authority.ordinal())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public IEntity saveElementWorkspace(IEntity element) {
        ElementPermission permission = new ElementPermission(element, Authority.OWN, this.getLoggedResearcher());
        this.elementPermissionService.save(permission);
        if (element.getMyType().equals(Experiment.class))
        {
            //this.experimentRepository.saveOrUpdate((Experiment) element);
            ((Experiment) element).getPermissions().add(permission);
            this.experimentRepository.saveOrUpdate((Experiment) element);
        }
        else if (element.getMyType().equals(Journal.class))
        {
            ((Journal) element).getPermissions().add(permission);
            this.journalRepository.saveOrUpdate((Journal) element);
        }
        else if (element.getMyType().equals(Document.class))
        {
            ((Document) element).getPermissions().add(permission);
            this.documentRepository.saveOrUpdate((Document) element);
        }
        else
        {
            // TODO: Error if other type
            return null;
        }
        return element;
    }

    @Override
    public IEntity updateElement(IEntity element, UUID uuid) {
        if (this.isResearcherAuthorized(Authority.WRITE, uuid, element.getMyType()))
        {
            return this.journalRepository.update((Journal) element, uuid);
        }
        else
        {
            // TODO: Error
            return null;
        }
    }

    @Override
    public <T extends IEntity> Set<T> getAuthorizedElement(String username, Authority authority, Class<T> type) {
        Set<T> result = new HashSet<>();
        Researcher researcher = this.getLoggedResearcher();

        // Loop permissions table
        for (ElementPermission elementPermission : this.elementPermissionService.get())
        {
            // Select all permissions of the logged researcher that are pointing to an entity of type and
            // that have an authority level below the required.
            if (elementPermission.getResearcher().equals(researcher)
                    && elementPermission.getMyType().equals(type)
                    && elementPermission.getAuthority().ordinal() >= authority.ordinal())
            {
                result.add((T)elementPermission.getElement());
            }
        }
        return result;
    }

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

    @Override
    public Set<Document> getAuthorizedDocument(Authority authority) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
        {
            return this.getAuthorizedElement(this.getLoggedResearcher().getEmail(), authority, Document.class);
        }
        else
        {
            //TODO: ERRR
            return null;
        }
    }
}
