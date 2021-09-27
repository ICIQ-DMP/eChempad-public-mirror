package org.ICIQ.eChempad.configurations;

import org.ICIQ.eChempad.entities.Researcher;
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
        this.researcherRepositoryClass.saveOrUpdate(new Researcher("Elvis", "Tech", null));
        this.researcherRepositoryClass.saveOrUpdate(new Researcher("Aitor", "Menta", null));
    }

}
