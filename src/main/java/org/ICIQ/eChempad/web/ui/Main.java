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
package org.ICIQ.eChempad.web.ui;

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
@Service
public class Main extends SelectorComposer<Window> {

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

	// Backend
	@Autowired
	private JournalService<Journal, UUID> journalService;

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
	
	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;

	//Report manager variables
	@Wire
	protected Div dcm;
	@Wire
	protected Div dcmPanelChildren;
	//Browser display components	
	@Wire
	protected Tabpanels mainTabTabpanels;
	@Wire
	protected Tab treeTab;
	@Wire
	protected Tab searchTab;
	@Wire
	protected Tab reportsTab;
	@Wire
	protected Borderlayout propertiesLayout;
	@Wire
	protected South bottomButtonsSouth;
	@Wire
	protected Div welcome;
	@Wire
	protected Tabbox mainTab;
	@Wire
	protected Radiogroup calcType;
	@Wire
	protected Vbox calcFile;
	@Wire
	protected Div calcUpload;
	@Wire
	protected Listbox selectedElements;
    @Wire
	protected Textbox calcName;
    @Wire
	protected Textbox calcDescription;
    @Wire
	protected Button uploadCalcBtn;
    @Wire
	protected Button resetUploadCalcBtn;
    @Wire
	protected East propertiesEast;
   
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
    
	@Listen("onClick=#treeTab")
	public void treeTabClick(){
		treeDivMaximize(isTreeFullSize);
		navigationQueue.publish(new Event("resetSearch"));
	}
	
	@Listen("onDoubleClick=#treeTab")
	public void treeTabDoubleClick(){
		isTreeFullSize = !isTreeFullSize;
		treeDivMaximize(isTreeFullSize);
		navigationQueue.publish(new Event("resetSearch"));
	}
	
	@Listen("onClick=#searchTab")
	public void searchTabClick(){	
		treeDivMaximize(isSearchFullSize);
		navigationQueue.publish(new Event("resetHome"));
	}
	
	@Listen("onDoubleClick=#searchTab")
	public void searchTabDoubleClick(){
		isSearchFullSize = !isSearchFullSize;
		treeDivMaximize(isSearchFullSize);		
		navigationQueue.publish(new Event("resetHome"));
	}

	@Listen("onClick=#reportsTab")
	public void reportstTabClick(){
		treeDivMaximize(true);
	}
	
    @Listen("onClick=#uploadCalcBtn")
    public void uploadCalcBtnClick() throws Exception{
    }

    @Listen("onClick=#resetUploadCalcBtn")
    public void resetUploadCalcBtnClick() throws IOException{
    	resetLoadCalcForm();
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
     	reportManagementQueue = EventQueues.lookup("reportmanagement", EventQueues.DESKTOP, true);
     	displayQueue = EventQueues.lookup("display", EventQueues.DESKTOP, true);
     	     	
     	navigationQueue.subscribe(new EventListener(){
     		@Override
     		public void onEvent(Event event) throws Exception {     			
     			if(event.getName().equals("resetHome"))
     				resetHome();     				
     			else if(event.getName().equals("showCalcUpload"))
     				showCalcUpload();
     			else if(event.getName().equals("displayNavigationElement")){
     				displayNavigationElement();
     			}
     			else if(event.getName().equals("displaySearchElement")){
     				displaySearchElement();     			
     			}
     		}
     	});
     	actionsQueue.subscribe(new EventListener(){
     		@Override 
     		public void onEvent(Event event) throws Exception {
     			if(event.getName().equals("dcmActionsMaximize")){
     				boolean maximize = (Boolean)event.getData();
     				dcmActionsMaximize(maximize);		     			
     			}
     		}
     	});     	 
     	reportManagementQueue.subscribe(new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("openReport")){
					HashMap<String, Object> parameters = (HashMap<String, Object>)event.getData();
					int reportId = (Integer)parameters.get("reportId");
					boolean appendSessionElements = parameters.containsKey("appendSessionElements")? (boolean)parameters.get("appendSessionElements"):false;					
				}else if(event.getName().equals("closeReport")){
					int reportId = (Integer)event.getData();							
				}else if(event.getName().equals("deleteReport")){
					int reportId = (Integer)event.getData();
				}
			}		     				     	
     	});
     	displayQueue.subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("addBootstrapClasses")) {
					setBootstrapClasses();
				}else if(event.getName().equals("hideNavigationButtons")) {
				}else if(event.getName().equals("showNavigationButtons")) {
				}
			}     		     		
     	});        	
    }
    
    @SuppressWarnings("unchecked")
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
	private void dcmActionsMaximize(boolean maximize){
		try{
			if(maximize){		
				propertiesEast.setSize("100%");	
				propertiesNorth.setSize("100%");			
			}else{
				propertiesEast.setSize("50%");
				propertiesNorth.setSize("65%");
			}
			setBootstrapClasses();
		}catch(Exception e){
			Logger.getGlobal().warning(e.getMessage());				// TODO: More than one desktop opened bug, we try to maximize a different Desktop element
		}
	}
	
    private void treeDivMaximize(boolean maximize){    	
		if (maximize){
			propertiesLayout.setVisible(false);
			propertiesEast.setSize("0%");
		}
		else{
			propertiesLayout.setVisible(true);
			propertiesEast.setSize("50%");
		}
		setBootstrapClasses();
    }
    
    private void resetHome(){
    	isTreeFullSize = false;
    	isSearchFullSize = false;
    	if(searchTab.isSelected())
    		treeDivMaximize(isSearchFullSize);
    	else if(treeTab.isSelected())
    		treeDivMaximize(isTreeFullSize);
    	else
    		treeDivMaximize(true);
    	displayWelcome();
    }
    
    private void displayWelcome(){     
 		this.calcUpload.setVisible(false);
		this.dcm.setVisible(false);
		this.welcome.setVisible(true);
		this.propertiesEast.invalidate();
    }
     
	private void showCalcUpload(){
		welcome.setVisible(false);
 		dcm.setVisible(false);
		calcUpload.setVisible(true);	
		propertiesEast.invalidate();			
	}
	
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
	
	@SuppressWarnings("unchecked")
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

     
    public void resetLoadCalcForm() throws IOException {

        this.parameterFile = new HashMap<>();
 		this.calcName.setText("");
 		this.calcDescription.setText("");
 		this.calcType.setSelectedItem(null);
 		Components.removeAllChildren(this.calcFile);
		for (Component component : this.calcUpload.queryAll(".uploadFileLabel")) {
			Label label = (Label) component;
			label.setValue("-");
		}
 	}

}

