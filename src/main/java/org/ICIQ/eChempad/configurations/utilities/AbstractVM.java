package org.ICIQ.eChempad.configurations.utilities;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.Executions;

public abstract class AbstractVM {

    public AbstractVM() {
        this.autowire(this);
    }

    protected final void autowire(Object object) {
        this.getApplicationContext().getAutowireCapableBeanFactory()
                .autowireBean(object);
        this.getApplicationContext().getAutowireCapableBeanFactory()
                .initializeBean(object, null);
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    protected final ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils
                .getRequiredWebApplicationContext(Executions.getCurrent()
                        .getDesktop().getWebApp().getServletContext());
    }
}