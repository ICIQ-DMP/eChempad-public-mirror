/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.web.ui.composers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.ResearcherService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.UUID;

/**
 * This class controls the JS / zul components of the profile page. This page is used to update and view your user data.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileComposer extends SelectorComposer<Window> {

    /**
     * Reference to the submit button, that updates the data of the currently logged user.
     */
    @Wire
    protected Button submitButton;

    /**
     * Reference to the reset button, that reloads the form.
     */
    @Wire
    protected Button resetButton;

    /**
     * Text box that holds the password of the logged user, as a password text box
     */
    @Wire
    protected Textbox password;

    /**
     * Text box that holds the username
     */
    @Wire
    protected Textbox username;

    /**
     * Text box that holds the signals API token, as a password text box
     */
    @Wire
    protected Textbox signalsToken;

    /**
     * Text box that holds the Dataverse API token, as a password text box
     */
    @Wire
    protected  Textbox dataverseToken;

    /**
     * Service to manipulate the DB with users.
     */
    @WireVariable("researcherService")
    private ResearcherService<Researcher, UUID> researcherService;

    /**
     * De facto constructor for Composer classes.
     *
     * @param comp Window component.
     * @throws Exception General exception.
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        // Propagate call to parent
        super.doAfterCompose(comp);

        // Populate the form with logged user data
        this.onResetButtonClick();
    }

    /**
     * Gets the current logged user as a Researcher instance.
     *
     * @return Researcher instance with the data of the logged user using the SecurityContextHolder
     */
    public Researcher getCurrentlyLoggedUser()
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.researcherService.loadUserByUsername(username);
    }

    /**
     * Deserialize data from a researcher instance passed by parameter into the UI.
     *
     * @param researcherData Instance of researcher received that needs to be unpacked over UI components.
     */
    public void putResearcherDataInForm(Researcher researcherData)
    {
        this.password.setValue(researcherData.getPassword());
        this.username.setValue(researcherData.getUsername());
        this.signalsToken.setValue(researcherData.getSignalsAPIKey());
        this.dataverseToken.setValue(researcherData.getDataverseAPIKey());
    }

    /**
     * Resets the form by reloading data from the DB of the currently logged user and unboxes it over the UI components.
     */
    @Listen("onClick = #resetButton")
    public void onResetButtonClick(){
        this.putResearcherDataInForm(this.getCurrentlyLoggedUser());
    }

    /**
     * Gets the data of the currently logged user, updates it with the content of the UI, updates DB and UI with
     * submitted data
     */
    @Listen("onClick = #submitButton")
    public void onSubmitButtonClick(){
        // Obtain username from security context and then load user instance information
        Researcher loggedUser = this.getCurrentlyLoggedUser();

        // Set properties from the form
        loggedUser.setPassword(this.password.getValue());
        loggedUser.setUsername(this.username.getValue());
        loggedUser.setSignalsAPIKey(this.signalsToken.getValue());
        loggedUser.setDataverseAPIKey(this.dataverseToken.getValue());

        // Update user
        this.researcherService.saveAndFlush(loggedUser);

        // Update form with new values
        this.putResearcherDataInForm(loggedUser);
    }
}
