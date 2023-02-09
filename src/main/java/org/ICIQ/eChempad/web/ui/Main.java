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
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class Main extends SelectorComposer<Window> {
	
	private Constants.ScreenSize layout = Constants.ScreenSize.X_LARGE;
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Main.class.getName());
	
    public static final String TMP_ROOT_DIR = WebApps.getCurrent().getRealPath("/") + "tmp/";
    
    private String userHome = null;
    private String userLogin = null;
     // ActionManager cm = null;
    private boolean isTreeFullSize = false;
    private boolean isSearchFullSize = true;

	private EventQueue<Event> navigationQueue = null; 
	private EventQueue<Event> actionsQueue = null;
	private EventQueue<Event> reportManagementQueue = null;
	private EventQueue<Event> displayQueue = null;
		
	private Constants.UploadType uploadType;
	private Long maxSystemFileSize;

	// Backend
	@Autowired
	private JournalService<Journal, UUID> journalService;

	@Wire
	Window mainWindow;
	
	//Small layout parent containers
	@Wire 
	Div mainSmall;
	@Wire 
	Div mainSmallInner;
	@Wire
	Div smallTreeLayout;
	@Wire
	Div smallProperties;
	@Wire
	Div smallItemDetails;
	@Wire
	Div mainSmallNavigation;
	
	//Large layout parent containers
	@Wire
	Borderlayout mainLarge;
	@Wire
	Center treeLayout;
	@Wire
	Div treeDiv;
	@Wire 
	North propertiesNorth;
	@Wire
	Div properties;
	@Wire
	Center itemDetails;
	
	//Report manager variables
	private HashMap<Integer,Tab> openReportTabs = null;
	private HashMap<Integer,Tabpanel> openReportTabpanels = null;	
	
	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;
	
	    
	@Wire
	Div dcm;
	@Wire
	Div dcmPanelChildren;
	//Browser display components	
	@Wire
	Tabpanels mainTabTabpanels;
	@Wire
	Tab treeTab;
	@Wire
    Tab searchTab;
	@Wire
	Tab reportsTab;
	@Wire
	Borderlayout propertiesLayout;
	@Wire
	South bottomButtonsSouth;	
	@Wire
	Div welcome;
	@Wire
	Tabbox mainTab;	
	@Wire 
	Radiogroup calcType;
	@Wire 
	Vbox calcFile;
	@Wire
	Div calcUpload;
	@Wire
	Listbox selectedElements; 
    @Wire 
    Textbox calcName;
    @Wire 
    Textbox calcDescription;
    @Wire
    Button uploadCalcBtn;
    @Wire 
    Button resetUploadCalcBtn;
    @Wire
    East propertiesEast;
   
    /////////////////////////Event listener associations///////////////////////////////////////////
       
    @Listen("onEditProfileClick=#mainWindow")
    public void onEditProfileClick(){
    	redirectToBrowseEditProfile();
    }
   
    @Listen("onBrowseClick=#mainWindow")
    public void browseClick(){
    	redirectToBrowse();
    }

    @Listen("onHelpClick=#mainWindow")
    public void helpBtnClick(){
    	openHelpPage();
	}
    @Listen("onFeedbackClick=#mainWindow")
    public void feedbackBtnClick(){
    	openFeedbackPage();
	}
    @Listen("onLogoutClick=#mainWindow")
    public void onLogoutClick(){
    	logout();
    }
    
    @Listen("onSizeLimitExceeded=#mainWindow")
    public void onSizeLimitExceeded() {       
        displayLimits();      
    }
    
	@Listen("onClick=#treeTab")
	public void treeTabClick(){
		treeDivMaximize(isTreeFullSize);
		enableNavigationButtons(true);
		navigationQueue.publish(new Event("resetSearch"));
	}
	
	@Listen("onDoubleClick=#treeTab")
	public void treeTabDoubleClick(){
		isTreeFullSize = !isTreeFullSize;
		treeDivMaximize(isTreeFullSize);
		enableNavigationButtons(true);
		navigationQueue.publish(new Event("resetSearch"));		
	}
	
	@Listen("onClick=#searchTab")
	public void searchTabClick(){	
		treeDivMaximize(isSearchFullSize);
		enableNavigationButtons(false);
		navigationQueue.publish(new Event("resetHome"));
	}
	
	@Listen("onDoubleClick=#searchTab")
	public void searchTabDoubleClick(){
		isSearchFullSize = !isSearchFullSize;
		treeDivMaximize(isSearchFullSize);		
		enableNavigationButtons(false);
		navigationQueue.publish(new Event("resetHome"));
	}

	@Listen("onClick=#reportsTab")
	public void reportstTabClick(){
		treeDivMaximize(true);
		enableNavigationButtons(false);
	}
	
    @Listen("onClick=#uploadCalcBtn")
    public void uploadCalcBtnClick() throws Exception{
    	loadCalc();	
    }

    @Listen("onClick=#resetUploadCalcBtn")
    public void resetUploadCalcBtnClick() throws IOException{
        if(tmpDir != null && tmpDir.contains(TMP_ROOT_DIR)) {
            FileUtils.deleteDirectory(new File(tmpDir));
        }
    	resetLoadCalcForm();
    }
	
	@Listen("onClick=#mainSmallNavigationPrevious") 
	public void onMainSmallNavigationPreviousClick() {
		Clients.evalJavaScript("moveLeft()");
	}
	
	@Listen("onClick=#mainSmallNavigationNext")
	public void onMainSmallNavigatioNextClick() {
		Clients.evalJavaScript("moveRight()");
	}


	//////////////////////////////////////////// Top toolbar actions //////////////////////////////////////////////////////////////////

	private void redirectToBrowseEditProfile() {
		Executions.sendRedirect(getBrowseEditProfileUrl());
	}
	
	private void redirectToBrowse() {
    	Executions.sendRedirect(getBrowseBaseUrl());
	}
	
	private void openHelpPage() {
    	String knowledgebaseUrl	= CustomProperties.getProperty("knowledgebase.url");
		Executions.getCurrent().sendRedirect(knowledgebaseUrl, "_blank");
	}
	
	private void openFeedbackPage() {
    	String knowledgebaseUrl	= CustomProperties.getProperty("feedback.url");
		Executions.getCurrent().sendRedirect(knowledgebaseUrl, "_blank");		
	}

	private void displayLimits() {

	    Long maxOutput = byteToMegabyte(getMaxFileSize(true, uploadType, maxSystemFileSize)); 
	    Long maxFile = byteToMegabyte(getMaxFileSize(false, uploadType, maxSystemFileSize));
	    
	    String message = (String)WebApps.getCurrent().getAttribute("uploadMaxFileSizeMessage");

	    StringBuilder cause = new StringBuilder();
	    if(maxOutput != -1 || maxFile != -1) {
	        cause.append("Your current upload limits are:");
	        cause.append("<ul>");
            cause.append("<li>Output file: " + (maxOutput == -1? " Unlimited" :  (maxOutput + " MB"))  + "</li>");
            cause.append("<li>Other files: " + (maxFile == -1? " Unlimited" : (maxFile + " MB"))  + "</li>");
            cause.append("</ul>");
	    } else {
	        cause.append("Current system limit is: " + byteToMegabyte(maxSystemFileSize) + " MB<br/>");
	        cause.append("Please contact the platform administrator for further information.");
	    }
        Window window = (Window) Executions.createComponents("errors/errorDialog.zul", null, null);
        window.setWidth("1000px");
        ErrorDialog errorDialog = (ErrorDialog) window.getAttribute("$composer");        
        errorDialog.setTitle("Upload size limit reached");
        errorDialog.setError("Provided files exceed the upload limits.");
        errorDialog.setCause(new Html(cause.toString()));        
        if(message != null && !message.isEmpty())
            errorDialog.setSolution(new Html(message));
        window.doModal();

	}
	
	public static String getBaseUrl() {
		String browseHostName 	= CustomProperties.getProperty("browse.server.hostname");
    	String browsePort		= CustomProperties.getProperty("browse.server.port");
    	String portDelimiter 	= browsePort.equals("")? "" : ":";    	
    	String browseUrl 		= "https://" + browseHostName + portDelimiter + browsePort;
    	return browseUrl;
	}
	
	public static String getBrowseEditProfileUrl() {
		String baseUrl = getBaseUrl();
    	String browseApp = CustomProperties.getProperty("browse.server.jspui.context");    	 
    	return baseUrl + "/" + browseApp + "/profile";
	}
	
	public static String getBrowseBaseUrl(){		    
    	String baseUrl = getBaseUrl();
    	String browseApp = CustomProperties.getProperty("browse.server.jspui.context");    	 
    	return baseUrl + "/" + browseApp;
	}
	
	private void logout(){
		Executions.sendRedirect(getBaseUrl() + "/create/logout");
	}
	
	public static String getHandleBaseUrl(){
		return getBrowseBaseUrl().concat(Constants.BROWSE_HANDLE_ENDPOINT);
	}

	///////////////////////////////////// Intialization functions //////////////////////////////////////////////////////////

	public void doAfterCompose(Window window) {      	
		try {			
			super.doAfterCompose(window);			
			setupLayoutListener(window);
	     	openReportTabs = new HashMap<Integer,Tab>();
	     	openReportTabpanels = new HashMap<Integer,Tabpanel>();
	 		initUserVars();
			initUploadRestrictions();
	    	initActionQueues();
			fillCalculationTypes();
			// cm = new ActionManager();
			navigationQueue.publish(new Event("resetHome"));
			navigationQueue.publish(new Event("newnavigation"));			
		} catch (Exception e) {
			logger.error(e.getMessage());			
		}
	}
    
	private void initUploadRestrictions() {
		String[] hardUsers = (String[])WebApps.getCurrent().getAttribute("uploadRestrictionUsersHard");
		uploadType = Arrays.asList(hardUsers).contains(userLogin)? Constants.UploadType.hard : Constants.UploadType.soft;
		maxSystemFileSize = (Long)WebApps.getCurrent().getAttribute("uploadMaxFileSize");
	}

	private void setupLayoutListener(Window window) {

		//Currently we only support large (Desktop) and small (mobile) display
		window.addEventListener(Events.ON_AFTER_SIZE, new EventListener<AfterSizeEvent>() {
			public void onEvent(AfterSizeEvent event) throws Exception {				
				if(event.getWidth()>=1200)
					handleLargeLayout(Constants.ScreenSize.X_LARGE);
				else if(event.getWidth()<1200 &&  event.getWidth()>=992)
					handleLargeLayout(Constants.ScreenSize.LARGE);
				else if(event.getWidth()<992 &&  event.getWidth()>=768)
					handleLargeLayout(Constants.ScreenSize.MEDIUM);
				else if(event.getWidth()<768 &&  event.getWidth()>=570)
					handleSmallLayout(event, Constants.ScreenSize.SMALL);
				else if(event.getWidth()<570)			
					handleSmallLayout(event, Constants.ScreenSize.X_SMALL);
				}
		});
		enableNavigationButtons(false);
	}

	private void handleLargeLayout(Constants.ScreenSize newSize) {
		layout = newSize;		
		mainSmall.setVisible(false);
		mainSmall.setSclass("none");
		swapChild(smallTreeLayout, treeLayout);
		swapChild(smallProperties, propertiesNorth);
		swapChild(smallItemDetails, itemDetails);			
		mainLarge.setVisible(true);			
		setBootstrapClasses();
		notifyDisplayHasChanged(layout);
	}
		
	private void handleSmallLayout(AfterSizeEvent event, Constants.ScreenSize newSize) {
		layout = newSize;
		mainLarge.setVisible(false);
		mainSmall.setVisible(true);
		swapChild(treeLayout, smallTreeLayout);
		swapChild(propertiesNorth, smallProperties);
		swapChild(itemDetails, smallItemDetails);						
		setSectionsHeight(event);		//Need to define fixed heights and widths, not working with hflex/vflex here 
		setBootstrapClasses();
		notifyDisplayHasChanged(layout);
	}
	
	private void setSectionsHeight(AfterSizeEvent event) {
		int height = event.getHeight() - 60;
		int width = event.getWidth();
		smallTreeLayout.setHeight(height + "px");
		smallProperties.setHeight(height + "px");
		smallItemDetails.setHeight(height + "px");
		smallTreeLayout.setWidth(width + "px");
		smallProperties.setWidth(width + "px");
		smallItemDetails.setWidth(width + "px");
		
	}

	private void notifyDisplayHasChanged(Constants.ScreenSize layout) {
		// displayQueue is noullpointer
		// displayQueue.publish(new Event("sizeChanged", null, layout));
	}

	private void swapChild(Component source, Component destination) {
		Component child = source.getFirstChild();
		if(child == null)
			return;
		child.detach();
		destination.appendChild(child);				
	}


    private void initUserVars() {  // throws BrowseCredentialsException
    	this.userLogin = SecurityContextHolder.getContext().getAuthentication().getName();  // ShiroManager.getCurrent().getUserName();
		this.userHome = getUserPath();
		Executions.getCurrent().getSession().setAttribute("username", this.userLogin);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void initActionQueues(){
     	//Load action queues
    	navigationQueue = EventQueues.lookup("navigation", EventQueues.DESKTOP, true);
     	actionsQueue = EventQueues.lookup("dcmactions", EventQueues.DESKTOP, true);
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
					openReport(reportId, appendSessionElements);					
				}else if(event.getName().equals("closeReport")){
					int reportId = (Integer)event.getData();							
					closeReport(reportId);
				}else if(event.getName().equals("deleteReport")){
					int reportId = (Integer)event.getData();
					closeReport(reportId);
				}
			}		     				     	
     	});
     	displayQueue.subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("addBootstrapClasses")) {
					setBootstrapClasses();
				}else if(event.getName().equals("hideNavigationButtons")) {
					enableNavigationButtons(false);
				}else if(event.getName().equals("showNavigationButtons")) {
					enableNavigationButtons(true);
				}
			}     		     		
     	});        	
    }
    
    @SuppressWarnings("unchecked")
	protected void fillCalculationTypes() throws InterruptedException{
		/*
        Long maxFileSize = increaseByteUnits(getMaxFileSize(false, uploadType, maxSystemFileSize));  // Get kilobytes for upload restriction
        Long maxOutputSize = increaseByteUnits(getMaxFileSize(true, uploadType, maxSystemFileSize));
    	List<CalculationType> types = CalculationTypeService.getAll();
    	for(CalculationType type : types) {
    		final Radio radio = calcType.appendItem(type.getName(), String.valueOf(type.getId()));
			radio.setChecked(false);      			   		
			radio.addEventListener(Events.ON_CHECK, new CalcTypeChangedListener(this, calcFile, maxFileSize, maxOutputSize));
			radio.setClass("uploadRadio");	
    	} */
    }

   ///////////////////// User related functions ////////////////////////////////////////////////////////    
	public String getUserHome(){
    	return userHome;
    }
	
	public static String getUserPath() {

		try{
			StringBuilder userPath = new StringBuilder();
			userPath.append(Constants.BASE_PATH);
			userPath.append(Constants.PATH_DELIMITER);
			userPath.append(SecurityContextHolder.getContext().getAuthentication().getName());
			return userPath.toString();    	     		
		}catch(Exception e){
			logger.error(e.getMessage());	
			return null;
		}
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
			logger.error(e.getMessage());				// TODO: More than one desktop opened bug, we try to maximize a different Desktop element
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
    	enableNavigationButtons(false);
    }
    
    private void displayWelcome(){     
 		calcUpload.setVisible(false);
 		dcm.setVisible(false); 		
 		welcome.setVisible(true);
 		propertiesEast.invalidate();
    }
     
	private void showCalcUpload(){
		welcome.setVisible(false);
 		dcm.setVisible(false);
		calcUpload.setVisible(true);	
		propertiesEast.invalidate();			
	}
	
	@SuppressWarnings("unchecked")
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
    
	// for loading calculation.
    private String tmpDir = null;
    private HashMap<String,String> parameterFile = null; 
     
    public void resetLoadCalcForm() throws IOException {
    	this.tmpDir = null;
        this.parameterFile = new HashMap<String,String>();
 		calcName.setText("");
 		calcDescription.setText("");
 		calcType.setSelectedItem(null);
 		Components.removeAllChildren(calcFile);
 		Iterator<Component> labels =  calcUpload.queryAll(".uploadFileLabel").iterator();
 		while(labels.hasNext()){
 			Label label = (Label)labels.next();
 			label.setValue("-");
 		}
 	}
     
    public void newTemporalUploadDirectory() throws IOException{
        if(tmpDir != null && tmpDir.contains(TMP_ROOT_DIR)) {
            FileUtils.deleteDirectory(new File(tmpDir));
        }
                
    	if(!new File(TMP_ROOT_DIR).exists())
    		new File(TMP_ROOT_DIR).mkdir();    	
     	
    	String temporalDir = TMP_ROOT_DIR + userLogin;
     	File dir = new File(temporalDir);
     	
     	// root directory for the user
     	if (!dir.isDirectory()) {
     		if (!dir.mkdir()) {
     			this.tmpDir = null;
     			this.parameterFile = null;
     			return;
     		}
     	}	
     	// generating the tmp dir into the userLogin dir.	
     	temporalDir = temporalDir + "/"  + UUID.randomUUID().toString();
     	dir = new File(temporalDir);
     	
     	if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
     			
     	if (!dir.mkdir()) {
     		this.tmpDir = null;
     		this.parameterFile = null;
     		return;
     	}
     	this.tmpDir = temporalDir;
     	this.parameterFile = new HashMap<String,String>();
    }
     
    public String getTmpDir(){
    	return this.tmpDir;
    }
     
    public void addParameterFile(String parameter, String file, boolean isRepeatable){
    	if(parameterFile.containsKey(parameter) && isRepeatable){
    		String previousFiles = parameterFile.get(parameter);
    		parameterFile.put(parameter, previousFiles + "#" + file);
    	}else{
        	parameterFile.put(parameter, file);    		
    	}
    }
     
    public void loadCalc() throws Exception{
		/*
    	calcName.setText(normalizeField(calcName.getText()));
     	if(calcName.getText().isEmpty() || calcDescription.getText().isEmpty()) {
     		Messagebox.show("Please fill name and description fields.", "Missing fields", Messagebox.OK, Messagebox.ERROR);
     		return;
     	}   
     	if(parameterFile == null) {
     		Messagebox.show("Please add the required files.", "Missing files", Messagebox.OK, Messagebox.ERROR);
     		return;
     	}
     	String currentPath = getCurrentPath();     	
      	HashMap<String,String> params = new HashMap<String,String>();     	
    	params.put(CalculationInsertion.PARAM_NAME,calcName.getText());
     	params.put(CalculationInsertion.PARAM_DESC,calcDescription.getText());
     	params.put(Constants.LOAD_CALC_TMP_DIR,getTmpDir());
     	params.put(Constants.LOAD_CALC_WEB, "true");
     	params.putAll(parameterFile);//Attach parameters and it's corresponding files
     	params.put(CalculationInsertion.PARAM_PATH, currentPath);
     	UploadService.loadCalculationViaWeb(params);     	
      	resetLoadCalcForm();  */
     } 
     
    private String getCurrentPath(){
		/*
 		Set<Entity> selectedElements = (Set<Entity>)_desktopScope.get("selectedElements");		
 		Entity dc = selectedElements.iterator().next(); 		
      	return dc.getPath(); */
		return "";
     }
      
    public void clearMetsFileOnContentManager(){
     	// this.cm.clear();
     }

	//////////////////////////////////////////// Form validation functions and utilities ////////////////////////////////////////////////////////////
    
    public static String normalizeField(String value){
        //return Command.normalizeField(value);
		return ";";
    }

    ///////////////////////////////// Report management functions //////////////////////////////////////////////////
     
	private void openReport(int reportId, boolean appendSessionElements) throws Exception{
		 treeDivMaximize(true);
		 Clients.evalJavaScript("clearMainTabsSelection();");
    	 if(openReportTabpanels.containsKey(reportId)){		//Select already existing report    		 
    		 Tab reportTab = openReportTabs.get(reportId);
    		 mainTab.setSelectedTab(reportTab);
    		 
    	 }else{
    		 createReportTab(reportId, appendSessionElements);
    	 }    	
    	 
     }
               
    private void closeReport(int reportId){    	
    	 try{
	    	 Tab reportTab = openReportTabs.get(reportId);
	    	 Tabpanel reportTabpanel = openReportTabpanels.get(reportId);
	    	 reportTabpanel.detach();
			 reportTab.detach(); 
			 mainTab.removeChild(reportTab);
			 mainTabTabpanels.removeChild(reportTabpanel);
			 openReportTabs.remove(reportId);
			 openReportTabpanels.remove(reportId);
			 mainTab.setSelectedTab(reportsTab);
    	 }catch(Exception e){

    	 }
    }

    private boolean reportHasUnsavedChanges(int reportId) {
		/*
    	Tabpanel reportTabpanel = openReportTabpanels.get(reportId);
    	ReportBase reportBase = (ReportBase)reportTabpanel.getFirstChild().getFirstChild().getAttribute("reportBaseWindow$composer");
    	return reportBase.hasChanges();  */
		return true;
    }

    private void displaySaveReportDialog(int reportId) {
		Window window = (Window) Executions.createComponents("errors/questionDialog.zul", null, null);
		QuestionDialog questionDialog = (QuestionDialog) window.getAttribute("$composer");
		questionDialog.setTitle("Report has changed");
		questionDialog.setContent("Current report " + reportId + " does contain unsaved changes",
									"Would you like to save them?",
									"Save", "Discard");
		questionDialog.setParameters(reportId);
		questionDialog.configEventQueue("reportmanagement", "saveAndCloseReport", "closeReport", "closeReport");
		window.doModal();
    }
   
    @SuppressWarnings("unchecked")
	private void createReportTab(int reportId, boolean appendSessionElements) {

    	 Tab newReportTab = new Tab();					//Add new report tab 
		 newReportTab.setSelected(true);
		 newReportTab.addEventListener("onClose", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Tab tab = (Tab)event.getTarget();
				int reportId = (Integer)tab.getAttribute("reportId");
				if(reportHasUnsavedChanges(reportId)) 
					displaySaveReportDialog(reportId);
				else
					reportManagementQueue.publish(new Event("closeReport", null, reportId));
			}
		 });    
		 newReportTab.addEventListener("onClick", new EventListener(){
			 @Override 
			 public void onEvent(Event event) throws Exception {
				 treeDivMaximize(true);
				 enableNavigationButtons(false);				 
				 //Clients.evalJavaScript("clearMainTabsSelection();");
				 //tab.setSelected(true);
			 }
		 });
		 //Display new tab and tabpanel		 		 
		 try{
			 String name = this.journalService.getById(UUID.fromString(String.valueOf(reportId))).getName();  // ReportService.getReport(reportId).getName();
			 newReportTab.setLabel(name.equals("")?String.valueOf(reportId):name);
		 }catch(Exception e){
			newReportTab.setLabel(String.valueOf(reportId));
		 }finally{
			 newReportTab.setAttribute("reportId", reportId);
		 }
		 
		 mainTab.getTabs().appendChild(newReportTab);
		 newReportTab.setClosable(true);    		     		 
		 Tabpanel newReportTabpane = new Tabpanel();    		 
		 Include include = new Include(Constants.REPORT_TEMPLATES_FOLDER + "/" + "reportBase.zul");
		 include.setDynamicProperty("reportId", reportId);								//Will be used to load report information inside reportBase controller		 
		 include.setDynamicProperty("appendSessionElements", appendSessionElements);	//Add selected elements
		 include.setMode("instant");
		 include.setParent(newReportTabpane);		 
		 newReportTabpane.appendChild(include);    		
		 mainTabTabpanels.appendChild(newReportTabpane);    		 
		 newReportTabpane.setParent(mainTabTabpanels);    		     		 
		 openReportTabs.put(reportId, newReportTab);
		 openReportTabpanels.put(reportId, newReportTabpane);
		 enableNavigationButtons(false);

     }
    
	private void enableNavigationButtons(boolean enableNavigation) {
		if(enableNavigation) {
			mainSmallNavigation.setVisible(true);
			smallProperties.setVisible(true);
			smallItemDetails.setVisible(true);						
		}else {
			mainSmallNavigation.setVisible(false);
			smallProperties.setVisible(false);
			smallItemDetails.setVisible(false);
		}		
	}
	

	/** Function that returns the configured maximum file size depending on its type and the user assigned quota.
	 * @param isOutput Selects the type of file is going to be determining its size
	 * @param uploadType Selects the type of quota assigned to the user: soft or hard.
	 * @return Max upload file size in kB, -1 if unlimited
	 */
	private Long getMaxFileSize(boolean isOutput, Constants.UploadType uploadType, Long fallbackValue) {
	    Long size = -1L;
	    if(isOutput) {
	        if(uploadType == Constants.UploadType.soft)
	            size = (Long)WebApps.getCurrent().getAttribute("uploadMaxOutputFileSizeSoft");  
	        else
	            size = (Long)WebApps.getCurrent().getAttribute("uploadMaxOutputFileSizeHard");
	    } else {
	        if(uploadType == Constants.UploadType.soft)
	            size = (Long)WebApps.getCurrent().getAttribute("uploadMaxFileSizeSoft");
	        else
	            size = (Long)WebApps.getCurrent().getAttribute("uploadMaxFileSizeHard");	        
	    }
	    return size == -1L? fallbackValue: size;
	}


	private Long byteToMegabyte(Long bytes) {
	    return increaseByteUnits(increaseByteUnits(bytes));
	}

	private Long increaseByteUnits(Long bytes) {
        return bytes != -1 ?  bytes / 1024 : bytes.intValue();       
    }
	
}

