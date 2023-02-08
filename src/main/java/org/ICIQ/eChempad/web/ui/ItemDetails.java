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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.Map;


public class ItemDetails extends SelectorComposer<Window> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(ItemDetails.class);
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static final String PARAM_ERROR = "Error in parameters.";
		
	@SuppressWarnings("rawtypes")
	private EventQueue navigationQueue = null;
	private String currentMode = "navigation";
		
	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;
	
	private int defaultPermission = 0;

	@Wire 
	Div itemDetailsDiv;
	@Wire
	Label hiddenID;	
	@Wire
	Label currentSelectionTitle;		
	@Wire
	Textbox name;	
	@Wire
	Textbox description;	
	@Wire 
	Label type;	
	@Wire
	Textbox path;	
	@Wire
	Listbox owner;	
	@Wire
	Listbox group;	
	@Wire 
	Listbox permissions;	
	@Wire
	Textbox cDate;	
	@Wire
	Textbox mDate;	
	@Wire 
	Textbox pDate;	
	@Wire
	Textbox conceptGroup;	
	@Wire
	Textbox state;
		
	@Wire 
	Div operationButtonsLayout;
	@Wire
	Button createProjectBtn;
	@Wire
	Button modifyBtn;
	@Wire
	Button removeBtn;

	
	@Listen("onClick=#createProjectBtn")
	public void createProjectClick() throws Exception{
		// createProject();
	}
	@Listen("onClick=#modifyBtn")
	public void modifyClick() throws InterruptedException, Exception{
		// modifyElement();
	}
	@Listen("onClick=#removeBtn")
	public void removeClick() throws InterruptedException{
		System.out.println("sfdsdf");
		// deleteSelectedElement();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);		
		initActionQueues();
		//fillListboxes();
		disableBottomButtons(true);
		defaultPermission = permissions.getSelectedIndex();
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void initActionQueues(){
		/*
		navigationQueue	= EventQueues.lookup("navigation", EventQueues.DESKTOP, true);
		navigationQueue.subscribe(new EventListener(){
			@Override
			public void onEvent(Event event) throws BrowseCredentialsException{
				if(event.getName().equals("resetHome")){
					resetHome();
				}else if(event.getName().equals("displaySearchElement")){
					currentMode = "search";
					Set<Entity> selectedElements = (Set<Entity>) _desktopScope.get("selectedSearchElements");
					Entity node = selectedElements.size() == 1 ?selectedElements.iterator().next(): null;
					displayElement((PublicableEntity)node);
					hideBottomButtons(true);
				}else if(event.getName().equals("displayNavigationElement")){
					currentMode = "navigation";
					Set<Entity> selectedElements = (Set<Entity>) _desktopScope.get("selectedElements");
					Entity node = selectedElements.size() == 1 ?selectedElements.iterator().next(): null;
					displayElement((PublicableEntity)node);
					hideBottomButtons(false);
				}else if(event.getName().equals("updateWithChildren")) {
					modifyProject((Project)event.getData(), true);
				}else if(event.getName().equals("updateWithoutChildren")) {
					modifyProject((Project)event.getData(), false);
                	}
			}
		}); */
	}
		
	/*
	private void fillListboxes() throws BrowseCredentialsException{
		ShiroManager manager = ShiroManager.getCurrent();
	 	for(String username: manager.getOwners())
    		owner.appendItem(username, String.valueOf(manager.getUserIdByName(username)));
	 	
	 	List<String> groupNames =Arrays.asList(manager.getGroups());
	 	List<String> userGroups = Arrays.asList(manager.getUserGroups());
	 	
    	for(String groupName : groupNames ){
    		String groupId = String.valueOf(manager.getGroupIdByName(groupName));    		
			Listitem item = group.appendItem(groupName, groupId);			
			item.setVisible(userGroups.contains(groupId));			
    	}    	
	}
	*/
	
	private void resetHome(){
		//currentSelectionTitle.setValue(Main.getUserPath());
     	hiddenID.setValue("");
     	name.setText("");
     	type.setValue("");
     	description.setText("");
     	conceptGroup.setText("");
     	path.setText("");
     	cDate.setText("");
     	mDate.setText("");
     	pDate.setText("");
     	state.setText("");     	
     	permissions.setSelectedIndex(defaultPermission);
     	createProjectBtn.setDisabled(false);
		modifyBtn.setDisabled(true);
		removeBtn.setDisabled(true);     	
	}	
	
	private void hideBottomButtons(boolean hide){		
		operationButtonsLayout.setVisible(!hide);
	}
	
	private void disableBottomButtons(boolean b){	
		createProjectBtn.setDisabled(b);
		modifyBtn.setDisabled(b);
		removeBtn.setDisabled(b);
    }

	private void setButtonsForElement(boolean isProject, boolean isPublished){	
		createProjectBtn.setDisabled(!isProject);		
		modifyBtn.setDisabled(isPublished);
		removeBtn.setDisabled(isPublished);	
	}
	
	/*
	private void displayElement(PublicableEntity dc) throws BrowseCredentialsException{
		if(dc == null){
			resetHome();
			disableBottomButtons(true);
			return;
		}
		
		this.hiddenID.setValue(String.valueOf(dc.getId()));
		this.name.setText(dc.getName());
		this.description.setText(dc.getDescription());
		this.type.setValue(getType(dc));		
		setPermissionFields(dc);
		this.cDate.setText(formatDate(dc.getCreationDate()));		
		this.permissions.setDisabled(dc.isCalculation());
		this.conceptGroup.setReadonly(dc.isCalculation());
		if(dc.isProject()) {
			Project project = (Project)dc;
			this.mDate.setText(formatDate(project.getModificationDate()));
			this.state.setText(project.getState());
			this.conceptGroup.setText(project.getConceptGroup());
		}		
		this.pDate.setText(formatDate(dc.getPublicationDate()));		
		this.path.setText(dc.getPath());
		this.currentSelectionTitle.setValue(dc.getPath());		
		enableGroupsList(dc);
		setButtonsForElement(dc.isProject(),dc.isPublished());
		itemDetailsDiv.invalidate();
	}

	private void setPermissionFields(Entity entity) {
		String permissions = "";
		try {
			Project project = entity.isProject()? (Project)entity : ProjectService.getByPath(entity.getParentPath());			
			permissions = Project.binaryPermissionToLinux(project.getPermissions());			
			setSelectedListItem(String.valueOf(project.getOwner()), this.owner);
			setSelectedListItem(String.valueOf(project.getGroup()), this.group);
			setSelectedListItem(permissions, this.permissions);
		}catch(Exception e) {
			log.error("Error loading item details for item " + entity.toString());
		}
	}
	
	
	private String getType(Entity entity) {
		if(entity instanceof Project)
			return "PRO";
		else if(entity instanceof Calculation) 
			return ((Calculation)entity).getType().getAbbreviation();
		return "";
	}
	
	private void setSelectedListItem(String selection,Listbox list){		
		List<Listitem> iList = list.getItems();
		Listitem lItem;
		for (int i=0;i<iList.size();i++){
			lItem = (Listitem)iList.get(i); 
			if (lItem.getValue().equals(selection)){
				list.selectItem(lItem);
				break;
			}
		}
	}

	private void enableGroupsList(Entity dc) throws BrowseCredentialsException{
		if(currentMode.equals("navigation")){
			group.setDisabled(dc.isCalculation());			
			String[] userGroups = ShiroManager.getCurrent().getUserGroups();
			
			String entityGroup = getEntityGroupName(dc);			
			for(Listitem item: group.getItems())
				try {
					if(ArrayUtils.contains(userGroups, item.getValue()) || entityGroup.equals(item.getValue())) //Groups user belong to or just element specific configuration
						item.setVisible(true);
					else
						item.setVisible(false);	
				}catch(Exception e) {
					item.setVisible(false);
				}									
		}else if(currentMode.equals("search")){
			for(Listitem item : group.getItems())
				item.setVisible(true);
			group.setDisabled(true);
		}		
	}
	
	private String getEntityGroupName(Entity entity) {
		try {
			if(entity.isProject())
				return ShiroManager.getCurrent().getGroupNameById(((Project)entity).getGroup());
			else if(entity.isCalculation()) {
				int groupId = ProjectService.getByPath(entity.getParentPath()).getGroup();
				return ShiroManager.getCurrent().getGroupNameById(groupId);							
			}			
		}catch(Exception e) {
			log.error("Error setting visible user groups on item display form for entity: "  + entity.toString());
		}
		return "";
	}

	private String formatDate(Timestamp date) {
		if(date == null)
			return "";
		return DATE_FORMATTER.format(date);
	}

    @SuppressWarnings("unchecked")
	private void createProject() throws Exception{
    	final String userHome = Main.getUserPath();
    	Entity oldData = null;

    	String elementId = hiddenID.getValue();
    	String userPath = "";
    	if (elementId.equals(""))
    		userPath = userHome;
    	else {
    		oldData = ProjectService.getById(Integer.valueOf(elementId));
    		userPath = isProjectSelected()? oldData.getPath(): oldData.getParentPath();    		
    	}
    	
    	name.setText(Main.normalizeField(name.getText()));
    	if(areCreateProjectParametersValid(userPath, userHome)){
    		Project project = new Project();
    		project.setParentPath(userPath);
    		project.setName(name.getText());
    		project.setDescription(description.getText());
    		project.setConceptGroup(conceptGroup.getText());
    		project.setPermissions(Project.linuxPermissionToBinary(((String)permissions.getSelectedItem().getValue())));
    		if(owner.getSelectedItem() == null) 
    			setSelectedListItem(String.valueOf(ShiroManager.getCurrent().getUserId()), this.owner);			
    		if(group.getSelectedItem() == null)
    			setSelectedListItem(String.valueOf(ShiroManager.getCurrent().getMainGroupId()), this.group);
    		
    		project.setOwner(Integer.valueOf((String)owner.getSelectedItem().getValue()));
       		project.setGroup(Integer.valueOf((String)group.getSelectedItem().getValue()));
       		try {
    			ProjectService.add(project);
    		}catch(Exception e) {
    			Messagebox.show(e.getMessage(), "Error while saving project", Messagebox.OK, Messagebox.INFORMATION);
    		}
    	}
    }
         
    private boolean isProjectSelected(){
    	return type.getValue().equals("PRO"); 
    }
    
    private boolean areCreateProjectParametersValid(String userPath, String userHome) throws WrongValueException, InterruptedException{
    	if  ((userPath.equals(userHome)) && (!path.getText().trim().equals(""))){
    		Messagebox.show("Path must be selected using the web interface.", PARAM_ERROR, Messagebox.OK, Messagebox.INFORMATION);
    		return false;    		
    	}    	 
    	if (userPath.endsWith("/")){
    		Messagebox.show("Path must not be ended by /", PARAM_ERROR, Messagebox.OK, Messagebox.INFORMATION);
    		return false;
    	}
    	if (name.getText().isEmpty() || name.getText().contains(" ")) {
    		Messagebox.show("Name field is mandatory and doesn't allow blank spaces, use underscores otherwise", PARAM_ERROR, Messagebox.OK, Messagebox.INFORMATION);
    		return false;
    	}
    	if (description.getText().isEmpty()) {
    		Messagebox.show("Description field is mandatory", PARAM_ERROR, Messagebox.OK, Messagebox.INFORMATION);
    		return false;
    	}
    	return true;
    }
   
    public void modifyElement() throws Exception {
    	if(getSelectedElementPath() == null) {
    		Messagebox.show("No selected Element, please, select one.","No selection",Messagebox.OK, Messagebox.ERROR);
    		return;
    	}    	
    	int entityId = Integer.valueOf(hiddenID.getValue());
    	if(isProjectSelected()) {    	    
    		Project project = ProjectService.getById(entityId);
    		if(havePermissionsChanged(project))     		
    		    showUpdateChildrenDialog(project);
    		else
    		    modifyProject(project, false);
    	} else {    	       		
    		modifyCalculation(CalculationService.getById(entityId));        	     		    
    	}    	
    }
    
    private void modifyCalculation(Calculation calculation) {        
        calculation.setDescription(description.getText());
        if (!(calculation.getName().equals(name.getText()))) {
            name.setText(Main.normalizeField(name.getText()));  
            calculation.setName(name.getText());
            TreeEvent.sendEventToUserQueue(new Event("resetHome", null, null));  // If path is replaced, refresh itemDetails and itemView
        }
        try {
            CalculationService.update(calculation);
        }catch(Exception e) {
            Messagebox.show(e.getMessage(), "Error updating element", Messagebox.OK, Messagebox.INFORMATION);
        }
    }
    private void modifyProject(Project project, boolean updateChildrenPermissions) {    
        project.setDescription(description.getText());
        project.setOwner(Integer.valueOf((String)owner.getSelectedItem().getValue()));
        project.setGroup(Integer.valueOf((String)group.getSelectedItem().getValue()));
        project.setConceptGroup(conceptGroup.getText());
        project.setPermissions(Project.linuxPermissionToBinary(((String)permissions.getSelectedItem().getValue())));            
        if (!(project.getName().equals(name.getText()))) {
            name.setText(Main.normalizeField(name.getText()));
            project.setName(name.getText());
            TreeEvent.sendEventToUserQueue(new Event("resetHome", null, null));  // If path is replaced, refresh itemDetails and itemView
        }
        try {
            ProjectService.update(project);
            if(updateChildrenPermissions) {                
                ProjectService.cascadePermissionsToChildren(project);
                TreeEvent.sendEventToUserQueue(new Event("refresh"));
            }
        } catch(Exception e) {
            Messagebox.show(e.getMessage(), "Error updating element", Messagebox.OK, Messagebox.INFORMATION);
        }
    }
    
    private boolean havePermissionsChanged(Project project) {
        return project.getGroup() != Integer.valueOf((String)group.getSelectedItem().getValue()) ||
                !project.getPermissions().equals(Project.linuxPermissionToBinary((String)permissions.getSelectedItem().getValue()));
    }

    private void showUpdateChildrenDialog(Project project) {
        Window window = (Window) Executions.createComponents("errors/questionDialog.zul", null, null);
        QuestionDialog questionDialog = (QuestionDialog) window.getAttribute("$composer");
        questionDialog.setTitle("Update children permissions");
        questionDialog.setContent("Current project has modified its access or its owner group.",
                                  "Should this changes be applied to all its children projects?",
                                  "Yes", "No");
        questionDialog.setParameters(project);
        questionDialog.configEventQueue("navigation", "updateWithChildren", "updateWithoutChildren");
        window.doModal();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	void deleteSelectedElement() throws InterruptedException{
    	if (getSelectedElementPath() == null)
    		Messagebox.show("No selected Element, please, select one.","No selection",Messagebox.OK, Messagebox.ERROR);    	
    	else
        	Messagebox.show("Confirm element removal: \n" + getSelectedElementPath() ,"Remove element",Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new DeleteElementListener());    	
    }

    public String getSelectedElementPath() throws InterruptedException {
    	if ((hiddenID.getValue() == null) || (hiddenID.getValue().equals(""))) 
    		return null;    	
    	int id = Integer.valueOf(hiddenID.getValue());    	    
    	if (isProjectSelected()) 	
    		return ProjectService.getById(id).getPath();
    	else 								
    		return CalculationService.getById(id).getPath();   	        
    }

    class DeleteElementListener implements EventListener{
		@Override
		public void onEvent(Event event) throws Exception {
		      if("onYes".equals(event.getName()))
              	delete(Integer.valueOf(hiddenID.getValue()));		     
		}

		private void delete(int id) throws InterruptedException{						
			try {
				if(isProjectSelected())
					ProjectService.deleteProject(id);
				else
					CalculationService.deleteCalculation(id);
			}catch(Exception e) {
				Messagebox.show(e.getMessage(), "Error removing element", Messagebox.OK, Messagebox.INFORMATION);
			}
			TreeEvent.sendEventToUserQueue(new Event("resetHome", null, null));
		}
    }
    	*/

}
