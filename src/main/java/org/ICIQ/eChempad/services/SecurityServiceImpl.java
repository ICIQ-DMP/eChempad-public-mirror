package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.EChempadApplication;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Repository
public class SecurityServiceImpl implements SecurityService{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Journal> getAuthorizedJournal(Authority authority) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
        {
            return getAuthorizedJournal(((UserDetails) principal).getUsername(), authority);
        }
        else
        {
            //TODO: ERRR
            return null;
        }
    }

    @Override
    public List<Journal> getAuthorizedJournal(String username, Authority authority) {
        String queryStr = " SELECT DISTINCT journal " +
                "FROM journal, researcher, elementpermission " +
                "WHERE elementpermission.researcher = " +
                "(SELECT researcher.uuid FROM researcher WHERE researcher.email = ?1) " +
                "AND elementpermission.journal_id = journal.uuid " +
                "AND elementpermission.authority >= ?2";
        try {
            Query query = entityManager.createNativeQuery(queryStr, Journal.class);
            query.setParameter(1, username);
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
}
