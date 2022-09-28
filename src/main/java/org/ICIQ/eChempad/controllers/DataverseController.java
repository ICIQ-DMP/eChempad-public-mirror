package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.ICIQ.eChempad.services.genericJPAServices.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class DataverseController {

    @Autowired
    private ResearcherService researcherService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private AclServiceCustomImpl aclRepository;

    public DataverseController(ResearcherService researcherService, JournalService journalService, AclServiceCustomImpl aclRepository) {
        this.researcherService = researcherService;
        this.journalService = journalService;
        this.aclRepository = aclRepository;
    }

    

    public ResearcherService getResearcherService() {
        return researcherService;
    }

    public void setResearcherService(ResearcherService researcherService) {
        this.researcherService = researcherService;
    }

    public JournalService getJournalService() {
        return journalService;
    }

    public void setJournalService(JournalService journalService) {
        this.journalService = journalService;
    }

    public AclServiceCustomImpl getAclRepository() {
        return aclRepository;
    }

    public void setAclRepository(AclServiceCustomImpl aclRepository) {
        this.aclRepository = aclRepository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataverseController that = (DataverseController) o;
        return Objects.equals(researcherService, that.researcherService) && Objects.equals(journalService, that.journalService) && Objects.equals(aclRepository, that.aclRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(researcherService, journalService, aclRepository);
    }

    @Override
    public String toString() {
        return "DataverseController{" +
                "researcherService=" + researcherService +
                ", journalService=" + journalService +
                ", aclRepository=" + aclRepository +
                '}';
    }
}
