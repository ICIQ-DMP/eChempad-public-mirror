/**
 * Create module - Create module inside the ioChem-BD software.
 * Copyright © 2014 ioChem-BD (contact@iochem-bd.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.web.ui.composers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.ICIQ.eChempad.web.definitions.Constants;
import org.ICIQ.eChempad.web.definitions.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This is a custom controller, which in ZK is called a composer. To implement it, extend SelectorComposer and bound
 * the type of component that you want to control with this class.
 * <p>
 * This class is used to answer all the events that are fired from the ZK UI programmatically from Java instead of
 * javascript, which is the usual way to control HTML elements.
 *
 * It also contains all the properties that we can control from the Java side annotated with the @wire annotation.
 * Modifying properties of those @wired objects will modify the properties of hte HTML components in the UI.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1.0
 */
public class MainComposer extends SelectorComposer<Window> {

	/**
	 * This instance variable is used to know what is the state of the screen size at any moment.
	 */
	private Constants.ScreenSize layout = Constants.ScreenSize.X_LARGE;

	/**
	 * Contains the String that represents the name of the user, which will be its ICIQ email.
	 */
    private String userName = null;

    private boolean isTreeFullSize = false;
    private boolean isSearchFullSize = true;

	// for loading calculation.
	private HashMap<String,String> parameterFile = null;
	private EventQueue<Event> navigationQueue = null;
	private EventQueue<Event> reportManagementQueue = null;
	private EventQueue<Event> displayQueue = null;

	private HashMap<Integer,Tab> openReportTabs = null;
	private HashMap<Integer,Tabpanel> openReportTabpanels = null;

	private Constants.UploadType uploadType;
	private Long maxSystemFileSize;

	/**
	 * Main windows component.
	 */
	@Wire
	protected Window mainWindow;
	
	//Large layout parent containers
	@Wire
	protected Borderlayout mainLarge;
	@Wire
	protected Center treeLayout;
	@Wire
	protected Div treeDiv;
	@Wire
	protected North propertiesNorth;
	@Wire
	protected Div properties;
	@Wire
	protected Center itemDetails;
	
	// @WireVariable("desktopScope")
	//private Map<String, Object> _desktopScope;

    /////////////////////////Event listener associations///////////////////////////////////////////
       
    @Listen("onEditProfileClick=#mainWindow")
    public void onEditProfileClick(){
		// Executions.sendRedirect(getBrowseEditProfileUrl());
    }
   
    @Listen("onBrowseClick=#mainWindow")
    public void browseClick(){
		// Executions.sendRedirect(getBrowseBaseUrl());
    }

    @Listen("onHelpClick=#mainWindow")
    public void helpBtnClick(){
		String knowledgebaseUrl	= CustomProperties.getProperty("knowledgebase.url");
		Executions.getCurrent().sendRedirect(knowledgebaseUrl, "_blank");
	}

    @Listen("onFeedbackClick=#mainWindow")
    public void feedbackBtnClick(){
		String knowledgebaseUrl	= CustomProperties.getProperty("feedback.url");
		Executions.getCurrent().sendRedirect(knowledgebaseUrl, "_blank");
	}

    @Listen("onLogoutClick=#mainWindow")
    public void onLogoutClick(){
		Executions.sendRedirect(getBaseUrl() + "/create/logout");
    }
    
    @Listen("onSizeLimitExceeded=#mainWindow")
    public void onSizeLimitExceeded() {
	}
	
	@Listen("onDoubleClick=#treeTab")
	public void treeTabDoubleClick(){
		isTreeFullSize = !isTreeFullSize;

		navigationQueue.publish(new Event("resetSearch"));
	}
	
	@Listen("onClick=#searchTab")
	public void searchTabClick(){	

		navigationQueue.publish(new Event("resetHome"));
	}

	@Listen("onClick=#reportsTab")
	public void reportstTabClick(){

	}
	
    @Listen("onClick=#uploadCalcBtn")
    public void uploadCalcBtnClick() throws Exception{
    }

    @Listen("onClick=#resetUploadCalcBtn")
    public void resetUploadCalcBtnClick() throws IOException{

    }


	/**
	 * Returns the base URL where eChempad is currently running from. Needs to be refactored in order to increase the
	 * separation of concerns since this information should come from a class specialized in retrieving constants from
	 * the application.properties file. Furthermore, this method is static...
	 *
	 * @return String containing the URL where eChempad is currently hosted.
	 */
	public static String getBaseUrl() {
    	return "https://localhost:8081";
	}


	///////////////////////////////////// Initialization functions //////////////////////////////////////////////////////////

	/**
	 * This method is called once after the ZUL file and its components have been rendered and composed in the UI page.
	 * @param window Component that we are handling.
	 * @throws Exception Throws exception is something goes wrong.
	 */
	public void doAfterCompose(Window window) throws Exception {
		// First thing to do is execute the same method in the parent
		super.doAfterCompose(window);

		// Append an event listener that gets called every time the UI is resized
		window.addEventListener(Events.ON_AFTER_SIZE, (EventListener<AfterSizeEvent>) event -> {});  // TODO empty lambda

		openReportTabs = new HashMap<Integer,Tab>();
		openReportTabpanels = new HashMap<Integer,Tabpanel>();
		initUserVars();
		initUploadRestrictions();
		initActionQueues();
		fillCalculationTypes();
		// cm = new ActionManager();
		navigationQueue.publish(new Event("resetHome"));
		navigationQueue.publish(new Event("newnavigation"));
	}
    
	private void initUploadRestrictions() {

	}


	



	private void swapChild(Component source, Component destination) {
		Component child = source.getFirstChild();
		if(child == null)
			return;
		child.detach();
		destination.appendChild(child);				
	}


	/**
	 * Used to initialize the variables that depend on the logged user in the front side.
	 */
    private void initUserVars() {  // throws BrowseCredentialsException
    	this.userName = SecurityContextHolder.getContext().getAuthentication().getName();  // ShiroManager.getCurrent().getUserName();
		Executions.getCurrent().getSession().setAttribute("username", this.userName);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void initActionQueues(){
     	//Load action queues
    	navigationQueue = EventQueues.lookup("navigation", EventQueues.DESKTOP, true);
		EventQueue<Event> actionsQueue = EventQueues.lookup("dcmactions", EventQueues.DESKTOP, true);
     	this.reportManagementQueue = EventQueues.lookup("reportmanagement", EventQueues.DESKTOP, true);
     	this.displayQueue = EventQueues.lookup("display", EventQueues.DESKTOP, true);
     	     	
     	this.navigationQueue.subscribe((EventListener) event -> {
			switch (event.getName()) {
				case "resetHome":
					break;
				case "showCalcUpload":
					break;
				case "displayNavigationElement":
					displayNavigationElement();
					break;
				case "displaySearchElement":
					displaySearchElement();
					break;
			}
		 });

     	actionsQueue.subscribe((EventListener) event -> {
			 if(event.getName().equals("dcmActionsMaximize")){
				 boolean maximize = (Boolean)event.getData();
			 }
		 });


     	this.displayQueue.subscribe((EventListener) event -> {
			switch (event.getName()) {
				case "addBootstrapClasses":
					setBootstrapClasses();
					break;
				case "hideNavigationButtons":
					break;
				case "showNavigationButtons":
					break;
			}
		 });
    }
    
	protected void fillCalculationTypes() throws InterruptedException{
		/*
        Long maxFileSize = increaseByteUnits(getMaxFileSize(false, this.uploadType, maxSystemFileSize));  // Get kilobytes for upload restriction
        Long maxOutputSize = increaseByteUnits(getMaxFileSize(true, this.uploadType, maxSystemFileSize));
    	List<CalculationType> types = CalculationTypeService.getAll();
    	for(CalculationType type : types) {
    		final Radio radio = calcType.appendItem(type.getName(), String.valueOf(type.getId()));
			radio.setChecked(false);      			   		
			radio.addEventListener(Events.ON_CHECK, new CalcTypeChangedListener(this, calcFile, maxFileSize, maxOutputSize));
			radio.setClass("uploadRadio");	
    	} */
    }


  
    ////////////////////////////////// Navigation queue actions ///////////////////////////////////////////////////////
	
	private void displayNavigationElement() throws InterruptedException, IOException{
		/*
		isTreeFullSize = false;
		treeDivMaximize(false);
		Set<Entity> selectedElements = (Set<Entity>) _desktopScope.get("selectedElements");
		if(selectedElements.size() != 1) {
			displayWelcome();
			enableNavigationButtons(false);			
		} else if(selectedElements.size() == 1){
			Entity dc = selectedElements.iterator().next();		
			if(dc instanceof Project)
				showCalcUpload();
			else
				displayActionManager(dc);
			enableNavigationButtons(true);
		} */
	}
	
	private void displaySearchElement() throws InterruptedException, IOException{
		/*
		isSearchFullSize = false;
		treeDivMaximize(false);
		Set<Entity> selectedElements = (Set<Entity>)_desktopScope.get("selectedSearchElements");		
		Entity entity = selectedElements.iterator().next();		
		if(entity instanceof Project)
			displayWelcome();
		else
			displayActionManager(entity);		
		enableNavigationButtons(true); */
	}
	
    ///////////////////////////////////////////HomeDetails functions////////////////////////////////////////////////
  
    /*public void displayActionManager(Entity entity) throws InterruptedException, IOException {
    	if(entity == null){
    		displayWelcome();
    		return;
    	}    	
    	
		welcome.setVisible(false);
		calcUpload.setVisible(false);
		dcm.setVisible(true);
     	
		if(entity instanceof Calculation){
			Calculation calculation = (Calculation)entity;
			if(calculation.getMetsXml() == null){	
				cm.clear();
				return;
			}	    			
		 	try {
		 		cm.loadMetsFile(calculation);	    
				if(cm.containsActions() && dcmPanelChildren.getChildren().isEmpty())	    						
						dcmPanelChildren.appendChild(cm.getContent());
				setBootstrapClasses();
			} catch (Exception e) { 					
				logger.error(e.getMessage());
			}    		
    	}
    	//propertiesEast.invalidate();    	
    }


     */
    private void setBootstrapClasses() {
    	Executions.getCurrent().getDesktop().setAttribute("display", layout);
    	if(layout == Constants.ScreenSize.X_LARGE || layout == Constants.ScreenSize.LARGE || layout == Constants.ScreenSize.MEDIUM)
    		Clients.evalJavaScript("addBootstrapClasses('large');");
    	else
    		Clients.evalJavaScript("addBootstrapClasses('small');");    	
    }
}

