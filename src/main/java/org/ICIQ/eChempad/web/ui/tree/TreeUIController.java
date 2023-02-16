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
package org.ICIQ.eChempad.web.ui.tree;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.*;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TreeUIController extends SelectorComposer<Window> {

	private EventQueue<Event> userEventsQueue = null;
	private EventQueue<Event> navigationQueue = null;
	private EventQueue<Event> elementPublishQueue = null;
	private EventQueue<Event> reportManagementQueue = null;
	private EventQueue<Event> displayQueue = null;

	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;
    
	@Wire
    private Window treeWindow;

	@Wire
    private Tree tree;

	@Wire
    private Treecol orderTreeCol;

	@Wire
    private Treecol descriptionTreecol;

	@Wire
    private Treecol creationdateTreecol;

	@Wire
    private Treecol stateTreecol;

	@Wire
    private Treecol handleTreecol;

	@Wire
    private Treecol publishedTreeCol;

	@Wire
    private Treecol editableTreeCol;
	
	
    @Wire
    private Menupopup treePopup;

	@Wire
    private Menuitem treeDivExpandChildElements;

	@Wire
    private Menuitem treeDivCollapseChildElements;

	@Wire
    private Menuitem treeDivSelectChildElements;

	@Wire
    private Menuitem treeDivUnselectChildElements;

	@Wire
    private Menuitem treeDivPublishElements;

	@Wire
    private Menupopup reportPopup;

	@Wire
    private Menupopup emptyTreePopup;

	@Wire
    private Popup thumbnailPopup;

	@Wire
    private Button refreshBtn;


    /**
     * This instantiates the journal service of the backend, providing indirect access to the database.
     *
     * TODO Services do not enforce any type of security, only controllers do but they are expected to be called from
     * a HTTP REST call. Develop a new layer of controllers that can be called programmatically and enforce security or
     * try to make work the controller layer that is already implemented programmatically.
     */
    @WireVariable
    @Autowired
    private JournalService<Journal, UUID> journalService;

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
        //wire service manually by calling Selectors API
        Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));

        return compInfo;
    }


    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

       /* TreeModel<Journal> model = new DefaultTreeModel<>(
                new DefaultTreeNode(null,
                        new DefaultTreeNode[] {
                                new DefaultTreeNode(new Journal("/doc", "Release and License Notes")),
                                new DefaultTreeNode(new Journal("/dist", "Distribution"),
                                        new DefaultTreeNode[] {
                                                new DefaultTreeNode(new Journal("/lib", "ZK Libraries"),
                                                        new DefaultTreeNode[] {
                                                                new DefaultTreeNode(new Journal("zcommon.jar", "ZK Common Library")),
                                                                new DefaultTreeNode(new Journal("zk.jar", "ZK Core Library"))
                                                        }),
                                                new DefaultTreeNode(new Journal("/src", "Source Code")),
                                                new DefaultTreeNode(new Journal("/xsd", "XSD Files"))
                                        })
                        }
                ));

        TreeModel<Journal> model2 = new DefaultTreeModel<>(
                new DefaultTreeNode(null,
                        new DefaultTreeNode[] {}));

        model2.

        DefaultTreeNode[] journalTreeNodes = new DefaultTreeNode[]{}; */

        for (Journal journal: this.journalService.findAll()) {
            DefaultTreeNode newNode = new DefaultTreeNode(journal);
            DefaultTreeNode root = (DefaultTreeNode) ((DefaultTreeModel) this.tree.getModel()).getRoot();
            root.add(newNode);
        }


        this.tree.setItemRenderer(new JournalTreeRenderer());
    }

/*
	@Listen("onClick=#treeDivPublishElements")
	public void onTreeDivPublishElementsClick() {
		publishElements();
	}
	
	@Listen("onClick=#refreshBtn")
	public void onRefreshBtnClick() {
		refresh();
	}
	
	@Listen("onOpen=#treePopup")	
	public void onTreePopupOpen() {
		if(!existsSelection()){
			treePopup.close();
			return;
		}			
		configurePopupOptions();	
	}
	
	@Listen("onSort=tree#tree > treecols > treecol")
	public void onTreecolSort() {
		navigationQueue.publish(new Event("resetHome"));	
	}
	
	@Listen("onClick=#treeDivExpandChildElements")
	public void onTreeDivExpandChildElementsClick() {
		Treeitem selected = tree.getSelectedItem();
		recurseItem(selected, true);		
	}

	@Listen("onClick=#treeDivCollapseChildElements")
	public void treeDivCollapseChildElement() {
		Treeitem selected = tree.getSelectedItem();
		recurseItem(selected, false);		
	}


	@Listen("onClick=#treeDivSelectChildElements")
	public void onTreeDivSelectChildElementsClick() {				
		CustomTreeModel treeModel = (CustomTreeModel) tree.getModel();
		Treeitem item = tree.getSelectedItem();
		recurseItem(item, true);		
		List<Component> children = Selectors.find(item, "treeitem");		
		HashSet<Entity> selectedData = new HashSet<Entity>();
		selectedData.add((Entity) item.getAttribute("entity"));
		item.setSelected(true);
		for(Component childObj : children){
			Treeitem child = (Treeitem) childObj;
			child.setSelected(true);			
			selectedData.add((Entity) child.getAttribute("entity"));										
		}			
		//treeModel.setSelection(selectedData);
		treeModel.setEnhancedSelection(selectedData);
		Events.sendEvent(new Event("onSelect", tree));
	}
	
	@Listen("onClick=#treeDivUnselectChildElements")
	public void onTreeDivUnselectChildElementsClick() {
		CustomTreeModel treeModel = (CustomTreeModel)tree.getModel();
		Treeitem  item = tree.getSelectedItem();
		List<Component> children = Selectors.find(item, "treeitem");
		for(Component childObj : children){
			Treeitem child = (Treeitem) childObj;
			child.setSelected(false);
			Entity modelElement = (Entity) child.getAttribute("entity");
			treeModel.removeFromSelection(modelElement);
		}
		Events.sendEvent(new Event("onSelect", tree));
	}
	
	@Listen("onOpen=#thumbnailPopup")
	public void showThumbnail(Event event) throws MalformedURLException{		
		Treeitem treeItem = (Treeitem)((OpenEvent) event).getReference();		
		if(treeItem != null){
			String id = ((Treecell)treeItem.getFirstChild().getLastChild()).getLabel();			
			String thumbnailBytes = getCalculationThumbnailAsString(Long.parseLong(id));
			((Html)thumbnailPopup.getChildren().get(0)).setContent("<img style='width:200px;height:200px' src='data:image/jpeg;base64," + thumbnailBytes + "' alt='No image available'></image>");  
		}
	}	
	
	private String getCalculationThumbnailAsString(long calculationId) {
		try {
			File file = AssetstoreService.getCalculationFileByName(calculationId, CalculationInsertion.THUMBNAIL_FILE_NAME);
			Path filePath = file.toPath();		
			return base64.encodeAsString(Files.readAllBytes(filePath));	
		}catch(Exception e) {
			return "";
		}		
	}
	





	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initActionQueues(){
		userEventsQueue = EventQueues.lookup(getUsername() + "userevents", WebApps.getCurrent(), true);
		userEventsQueue.subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {				
				switch(event.getName()) {
					case "projectAdded":		appendProject((String) event.getData());
												break;
					case "calculationAdded":	appendCalculation((String) event.getData());
												break;
					case "projectModified":     updateProject((HashMap<String,String>) event.getData());					
												break;
					case "calculationModified": updateCalculation((HashMap<String,String>) event.getData());
												break;
					case "projectDeleted":		deleteProject((String) event.getData());
												break;
					case "calculationDeleted" :	deleteCalculation((String) event.getData());
												break;
					case "checkRefreshNeeded":	if(((TreeNavigationModel)(((CustomTreeModel)tree.getModel()))).needsRefresh() && !movingElements)
												refresh();
												break;					
					case "projectReorder":	 	reorderProjects((HashMap<String,Object>) event.getData());
												break;												
					case "calculationReorder":  reorderCalculation((HashMap<String,Object>) event.getData());
												break;				
					case "resetHome":			navigationQueue.publish(new Event("resetHome"));
												reorderTree();
												break;
					case "refresh":				refresh();
												break;
					case "fileRetrieved":       String[] values = ((String)event.getData()).split("#");
                                                notifyFileRetrieved(values[0], values[1]);
                                                break;
					
				}
			}
		});		
		navigationQueue = EventQueues.lookup("navigation", EventQueues.DESKTOP, true);
		navigationQueue.subscribe(new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				HashMap<String, Object> parameters = null;								 		
				switch(event.getName()) {
					case "resetHome":		resetHome();
											break;
					case "newnavigation":	newNavigation();
											break;
					case "movetoproject":	parameters = (HashMap<String, Object>) event.getData();
											Entity project = (Entity) parameters.get("destinationItem");
											Set<Object> selectedElements = (Set<Object>) parameters.get("sourceItems");
											moveMultipleElementsToProject(project, selectedElements);
											break;
					case "movetocalculation":	parameters = (HashMap<String, Object>) event.getData();
												Calculation source = (Calculation) parameters.get("sourceItems");
												Calculation destination = (Calculation) parameters.get("destinationItem");
												moveOverCalculation(source, destination);
												break;
					case "selectelementchildren":		onTreeDivSelectChildElementsClick();			// Only one project selected, select all children
					case "checkParentPublished":
					                                    if(selectTopParents() > 1)
					                                        Messagebox.show("Can't publish content from unrelated projects, please select the content to publish from the same project.", "Multiple unrelated projects selected", Messagebox.OK, Messagebox.INFORMATION);  
					                                    else
					                                        elementPublishQueue.publish(new Event("show")); //showPublishedParentsDialog();
														break;
					case "showPublish":		elementPublishQueue.publish(new Event("show"));
											break;
																			
				}
			}
		});
		elementPublishQueue	= EventQueues.lookup("elementpublish", EventQueues.DESKTOP, true);
        reportManagementQueue = EventQueues.lookup("reportmanagement", EventQueues.DESKTOP,true);
        displayQueue = EventQueues.lookup("display", EventQueues.DESKTOP,true);
        displayQueue.subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				switch(event.getName()) {
					case "sizeChanged":	ScreenSize size = (ScreenSize)event.getData();  
										setupLayout(size);	
				}				
			}        	
        });
	}


	protected void moveOverCalculation(Calculation source, Calculation destination) {
    	try {
    		CalculationService.moveOverCalculation(source, destination);
    	}catch(Exception e) {
    		Messagebox.show(e.getMessage(), "Error updating element", Messagebox.OK, Messagebox.INFORMATION);	
    	}		
	}

	private void setupLayout(ScreenSize size) {
		boolean isSmallLayout = (size == ScreenSize.SMALL || size == ScreenSize.X_SMALL);
		descriptionTreecol.setVisible(!isSmallLayout);
		creationdateTreecol.setVisible(!isSmallLayout);
		handleTreecol.setVisible(!isSmallLayout);
		publishedTreeCol.setVisible(!isSmallLayout);
		editableTreeCol.setVisible(!isSmallLayout);
	}
	
	private void appendProject(String projectId) throws InterruptedException {
		if(projectId == null)
			return;
		Entity project = ProjectService.getById(Integer.valueOf(projectId));
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).appendProject(project);
		reorderSubtree("appendProject", project);		
	}
	
	private void appendCalculation(String calcId) throws InterruptedException {
		if(calcId == null)
			return;
		Entity calculation = CalculationService.getById(Integer.valueOf(calcId));
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).appendCalculation(calculation);							
	}
	
	private void updateCalculation(HashMap<String,String> params) throws InterruptedException {
		int id = Integer.valueOf(params.get(Queries.CALCULATIONS_ID_COLUMN));
		String oldPath = params.get("oldPath");
		Entity calculation = CalculationService.getById(id);
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).updateCalculation(oldPath, calculation);
		TreeEvent.sendEventToUserQueue(new Event("checkRefreshNeeded", null, null));				
	}

	private void updateProject(HashMap<String, String> params) throws InterruptedException {
		int id = Integer.valueOf(params.get(Queries.PROJECTS_ID_COLUMN));
		Entity project = ProjectService.getById(id);
		String oldPath = params.get("oldPath");
		String newPath = params.get("newPath");		
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).updateProject(project, oldPath, newPath);
		TreeEvent.sendEventToUserQueue(new Event("checkRefreshNeeded", null, null));
	}
	
	protected void deleteProject(String projectPath) {
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).deleteProject(projectPath);
	}
	
	protected void deleteCalculation(String calculationPath) {
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).deleteCalculation(calculationPath);
	}
	
	protected void reorderProjects(HashMap<String, Object> params) throws InterruptedException {
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).reorderProjectsOnMove(params);
		reorderSubtree("reorderProject", params);
	}

	private void reorderCalculation(HashMap<String, Object> params) {
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).reorderCalculationsOnMove(params);
		reorderSubtree("reorderCalculation", params);
	}
	
	private String getUsername() {		
		try {
			return ShiroManager.getCurrent().getUserName();	
		}catch(Exception e) {
			return UUID.randomUUID().toString();
		}						
	}



	private void initTree(){	
		if(isMobileDevice()){	//Hide the rest of 
			Iterable<Component> childrenTreecols = tree.queryAll("treecol");
			for(Component children : childrenTreecols){
				Treecol treecol = (Treecol) children;
				treecol.setVisible(treecol.getId().equals("nameTreecol"));
			}			
		}		
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadReportTypes(){
		List<ReportType> reportTypes = ReportTypeService.getActiveReportTypes();
		for(ReportType reportType : reportTypes){			
			Menuitem menuitem = new Menuitem();
			menuitem.setLabel(reportType.getName());
			menuitem.setAttribute("reportType", reportType);
			menuitem.addEventListener("onClick", new EventListener(){
				@Override
				public void onEvent(Event event) throws Exception {
					Menuitem menuitem = (Menuitem)event.getTarget();
					int reportType = ((ReportType)menuitem.getAttribute("reportType")).getId();
					reportManagementQueue.publish(new Event("createreportfromselection", null, reportType));					
				}			
			});
			reportPopup.appendChild(menuitem);			
		}
	}
	
	private void refresh(){
		navigationQueue.publish(new Event("resetHome"));
		navigationQueue.publish(new Event("newnavigation"));
	}

    private void notifyFileRetrieved(String calcId, String calcFileId) {
        Component window = org.zkoss.zk.ui.Path.getComponent("/mainWindow");
        Component component = window.query("div#download-spin-" + calcFileId);
        if(component != null) {
            Div div = (Div)component;
            div.setClass("mx-auto fas fa-cloud-download-alt");
            div.setTooltiptext("File available");
        }

        Calculation calc = CalculationService.getById(Long.parseLong(calcId));
        String message = "Files from calculation:<br/><b>" + calc.getPath() + "</b><br/>Are now available to download.";
        Clients.evalJavaScript("setupToast('" + message +"');");
    }

	private void reorderTree() {				
		((TreeNavigationModel)(((CustomTreeModel) tree.getModel()))).sort(orderColumnComparator, true);
	}
	
	private void reorderSubtree(String action, Object param) {
		TreeNavigationModel navigationModel = ((TreeNavigationModel)(((CustomTreeModel) tree.getModel())));		
		if(action.equals("appendProject")) {
			Entity project = (Entity) param;
			TreeNode<Entity> parentTreeNode = navigationModel.findProjectNodeByPath(project.getParentPath());
			navigationModel.sort(orderColumnComparator, movingElements, parentTreeNode);
		}else if(action.equals("reorderProject") || action.equals("reorderCalculation")) {
			HashMap<String, Object> params = (HashMap<String, Object>) param;
			String oldParentPath = (String)params.get("oldParentPath");
			String newParentPath = (String)params.get("newParentPath");
			TreeNode<Entity> parentTreeNode = navigationModel.findProjectNodeByPath(oldParentPath);		
			navigationModel.sort(orderColumnComparator, movingElements, parentTreeNode);			
			if(!oldParentPath.equals(newParentPath)) { // Moving element between different projects
				parentTreeNode = navigationModel.findProjectNodeByPath(newParentPath);
				navigationModel.sort(orderColumnComparator, movingElements, parentTreeNode);
			}
		}
	}

	private void resetHome(){
		CustomTreeModel model = ((CustomTreeModel)tree.getModel());
		tree.setSelectedItem(null);
		if(model != null)
			model.clearSelection();	
	}
	
	private boolean isSingleProjectSelected() {
		Set<Object> selectedElements = ((CustomTreeModel)tree.getModel()).getSelection();		
		if(selectedElements.size() == 1) {		
			return ((Entity)((CustomTreeNode)selectedElements.iterator().next()).getData()).isProject();
		}
		return false;
	}
	
	private void showSelectChildDialog() {		
		Window window = (Window) Executions.createComponents("errors/questionDialog.zul", null, null);
		QuestionDialog questionDialog = (QuestionDialog) window.getAttribute("$composer");		
		questionDialog.setTitle("Empty project selected");
		questionDialog.setContent("Current selection only contains a project but none of its child calculations. It will generate a public collection with no calculations inside.", 
									"Would you like to extend the selection to all child calculations of the project?",
									"Yes", "No");
		questionDialog.configEventQueue("navigation", "selectelementchildren", "checkParentPublished");		
		window.doModal();
	}
	
	private int selectTopParents() {
		// Will get the count of top projects of current selection (in case there are sub-projects of a published project)
	    // If the parent is published, it will also select all intermediate projects.	    
	    	    
		Set<Object> selectedElements = ((CustomTreeModel)tree.getModel()).getSelection();
		HashMap<String, CustomTreeNode> projects = new HashMap<String, CustomTreeNode>();

		for(Object objElement :selectedElements) {
		    CustomTreeNode element =(CustomTreeNode) objElement; 		    		    
			Entity entity = (Entity)element.getData();
			if(entity.isProject())
				projects.put(entity.getPath(), element);
			else if(!projects.containsKey(entity.getParentPath())){	 // Add item parent
			    projects.put(entity.getParentPath(), (CustomTreeNode)element.getParent());
			}
		}
		
		HashMap<String, Integer> handles = new HashMap<>();
		SortedMap<Integer, List<CustomTreeNode>> paths = new TreeMap<>();		
		
		for(CustomTreeNode node: projects.values()) {
		    String handle = getParentPublicationHandle(node);
		    if(handle != null) {		        
		        selectParents(handle, node);                      // Selected a published project
		        handles.put(handle, 0);
		    }else {
		        String path = ((Entity)node.getData()).getPath(); // Selected an unpublished project
		        int level = path.split("/").length;               // Get its nesting level and store it
		        if(!paths.containsKey(level))
		            paths.put(level, new ArrayList<CustomTreeNode>());
		        paths.get(level).add(node);		        
		    }
		}
		Events.sendEvent(new Event("onSelect", tree));
		return handles.size() + countLevels(paths);
	}
	
	private int countLevels(SortedMap<Integer, List<CustomTreeNode>> paths) {	   
	    if(paths.size() == 0)                                  
	        return 0;
	    // Multiple top level paths
        if(paths.get(paths.firstKey()).size() != 1)        
            return paths.get(paths.firstKey()).size();
        // Only one top level path, check all lower level paths are children of it            	
        Iterator<Integer> pathIterator = paths.keySet().iterator();    
        int basePathLevel = pathIterator.next();
        String basePath = ((Entity)paths.get(basePathLevel).get(0).getData()).getPath();       // Retrieve top level path        
        while(pathIterator.hasNext()) {
            int currentPathLevel = pathIterator.next();
            Iterator<CustomTreeNode> levelPathsIterator = paths.get(currentPathLevel).iterator(); 
            while(levelPathsIterator.hasNext()) {
                CustomTreeNode levelNode = levelPathsIterator.next();
                if(!(((Entity)levelNode.getData()).getPath().startsWith(basePath + "/")))
                    return 2;                                           // At least there are two different top level paths selected 
                else {                    
                    for(int parentLevel = currentPathLevel; parentLevel > basePathLevel; parentLevel--) {
                        ((CustomTreeModel)tree.getModel()).addToSelection(levelNode);
                        levelNode = (CustomTreeNode) levelNode.getParent();
                    }
                }
                    
            }	               
        }
        
        Events.sendEvent(new Event("onSelect", tree));
        return 1; 
	}
	
	private String getParentPublicationHandle(CustomTreeNode node) {	    
	    CustomTreeNode parent = node;
	    while(parent != null) {	        
	        if(((PublicableEntity)parent.getData()).isPublished())
	            return ((PublicableEntity)parent.getData()).getHandle();
	        parent = (CustomTreeNode)parent.getParent();
	    }
	    return null;
	}
	
	private void selectParents(String handle, CustomTreeNode node) {	    
	    CustomTreeNode parent = node;
	    while(parent != null) {
	        PublicableEntity project = ((PublicableEntity)parent.getData());	        
	        if(project.getId() == 0 || (!project.getHandle().isEmpty() && !project.getHandle().equals(handle)))  // Reached top tree element or a parent with different handle
	            return;
	        ((CustomTreeModel)tree.getModel()).addToSelection(parent);
            parent = (CustomTreeNode)parent.getParent();
            if(!project.getHandle().isEmpty() && parent != null && (((PublicableEntity)parent.getData()).getHandle() == null || ((PublicableEntity)parent.getData()).getHandle().isEmpty()))     // Reached a parent that is not published, current element must be the root parent
                return;
        }
	}
	
	private void publishElements(){	
        if(isSingleProjectSelected())
            showSelectChildDialog();
        else
            navigationQueue.publish(new Event("checkParentPublished"));       
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void newNavigation() throws BrowseCredentialsException{
		TreeItemNavigationRenderer itemRenderer = new TreeItemNavigationRenderer(isTactileDevice());		
		for(EventListener listener : tree.getEventListeners("onSelect"))
			tree.removeEventListener("onSelect", listener);
		for(EventListener listener : tree.getEventListeners("onDrop"))
			tree.removeEventListener("onDrop", listener);
		
		
		TreeNavigationModel model = new TreeNavigationModel(new CustomTreeNode<Entity>(buildRootProject(), null, false));		
		model.setMultiple(true);
		tree.addEventListener("onSelect", new ItemSelectEvent());
		tree.setModel(model);			
		tree.setItemRenderer(itemRenderer);
		tree.setDroppable("true");
		tree.addEventListener("onDrop", new TreeDropEventListener());
		reorderTree();
	}
	
	@SuppressWarnings("unchecked")
	private void moveMultipleElementsToProject(Entity destination, Set<Object> selectedElements) throws InterruptedException{		
		if(!isMoveValid(destination, selectedElements))					
			return;
		if(selectedElements.size() > 0) 			
			Messagebox.show("Move selected items to "+ destination.getPath() + "?", "Move elements", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new OnMoveListener(selectedElements, destination));
	}
	
	private void configurePopupOptions(){			
		List<Component> components = Selectors.find(treePopup, ".treeWithSelection");
		for(Component component : components)			
			component.setVisible(existsSelection());	
		components = Selectors.find(treePopup, ".treeWithSingleSelection");				
		boolean isProjectSelected = isSingleProjectSelected();
		for(Component component : components)			
			component.setVisible(isProjectSelected);		
	}
		
	private void recurseItem(Component item, boolean open){
		boolean isProject = ((PublicableEntity)item.getAttribute("entity")).isProject();
		if(isProject) {
			Treeitem treeitem = (Treeitem)item;
			treeitem.setOpen(open);
			Treechildren children = getTreeitemChildren(treeitem);			
			if (children != null) 
				for (Iterator<?> iterator = children.getChildren().iterator(); iterator.hasNext();) 				
					recurseItem((Component) iterator.next(), open);	
		}
	}
	
	private Treechildren getTreeitemChildren(Treeitem item) {
		Collection<?> com = item.getChildren();
		for (Iterator<?> iterator = com.iterator(); iterator.hasNext();) {
			Component child = (Component)iterator.next(); 
			if(child instanceof Treechildren)
				return (Treechildren) child;
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	class ItemSelectEvent implements EventListener{
		@Override
		public void onEvent(Event arg0) throws Exception {
			displayElement();
		}		
	};	
    
	private void displayElement() {
		Set<Object> nodes = ((CustomTreeModel)tree.getModel()).getSelection();		
		Set<Object> data = new LinkedHashSet<Object>();			
		Iterator iter = nodes.iterator();		
		while(iter.hasNext()) {
			Object node = iter.next();
			if(node != null)
				data.add(((TreeNode<Entity>)node).getData());
		}
		_desktopScope.put("selectedElements", data);
		navigationQueue.publish(new Event("displayNavigationElement"));		
		tree.setContext(existsSelection()?treePopup:emptyTreePopup);
	}
	
	private boolean existsSelection(){
		return tree.getSelectedItems().size() != 0;
	}
	
	private boolean isTactileDevice(){		
		String displayDevice = (String) Executions.getCurrent().getSession().getAttribute("displayDevice");		
		if(displayDevice == null)
			return false;
		else if(displayDevice.equals("mobile") || displayDevice.equals("tablet"))
			return true;
		return false;
	}



	private boolean isMobileDevice(){		
		String displayDevice = (String) Executions.getCurrent().getSession().getAttribute("displayDevice");		
		if(displayDevice == null)
			return false;
		else if(displayDevice.equals("mobile"))
			return true;
		return false;
	}

	@SuppressWarnings("rawtypes")
	private class OnMoveListener implements EventListener{
		private Set<Object> selectedElements;
		private Entity destination;
		
		public OnMoveListener(Set<Object> selectedElements, Entity destination){
			this.selectedElements = selectedElements;
			this.destination = destination;
		}		
		@Override
		public void onEvent(Event event) throws Exception {
			if(Messagebox.ON_YES.equals(event.getName())){
				movingElements = true;				
				for(Object element : selectedElements){		// If move action accepted send move commands and last one to check whether tree needs to be reloaded  
					Entity source;
					if(element instanceof TreeNode)
						source = ((TreeNode<Entity>)element).getData();
					else
						source = (Entity) element;					
					try{
						moveToProject(source, destination);	
					}catch(Exception e){
						logger.error(e.getMessage());
					}			
				}				
				movingElements = false;
				TreeEvent.sendEventToUserQueue(new Event("checkRefreshNeeded", null, null));
				navigationQueue.publish(new Event("resetHome"));
			}
		}
	}
	
	private boolean isMoveValid(Entity destination, Set<Object> selectedElements){
		for(Object element: selectedElements) {
			Entity source = element instanceof TreeNode ? ((TreeNode<Entity>)element).getData():(Entity) element;
			boolean moveInvalid = isMovingToSameParentOrItself(source, destination) ||
					 				isDestinationChildOfSelection(source, destination) ||
					 				existsCollisionsOnDestinationPath(source, destination);
			if(moveInvalid)
				return false;
		}
		return true;
	}

	private boolean isMovingToSameParentOrItself(Entity source, Entity destination){		
		return destination.equals(source) || (source.getPath().equals(destination.getPath()) && source.isProject());			
	}

	//This function checks that among selected elements there is no parent of destination element, otherwise it will cause to break path hierarchy and result on orphaned elements.  
	private boolean isDestinationChildOfSelection(Entity source, Entity destination){			
		if(Paths.isDescendant(source.getPath(), destination.getPath()) && source.isProject() ){
			Messagebox.show("Can't move a parent project inside its child, please check your selection", "Move elements error", Messagebox.OK, Messagebox.ERROR, null);
			return true;
		}	
		return false;		
	}	
	
	private boolean existsCollisionsOnDestinationPath(Entity source, Entity destination){
		try{
			if((source.isProject() && ProjectService.projectExists(destination.getPath(), source.getName())) ||					
					(source.isCalculation() && CalculationService.calculationExists(destination.getPath(), source.getName()))){
				Messagebox.show("A project/calculation with the same name already exists on destination project. \nPlease rename colliding elements before moving them.", "Move elements error", Messagebox.OK, Messagebox.ERROR, null);					
				return true;				
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			Messagebox.show("An error raised moving selection", "Move elements error", Messagebox.OK, Messagebox.ERROR, null);
			return true;
		}		
		return false;
	}

	private void moveToProject(Entity source, Entity destination) throws InterruptedException{		
        try {
        	if(source.isProject())         		
        		ProjectService.moveOverProject((Project)source, (Project)destination);        	
        	else 
    			CalculationService.moveOverProject((Calculation)source, (Project)destination);        	
        }catch(Exception e) {
        	Messagebox.show(e.getMessage(), "Error while moving elements to project.", Messagebox.OK, Messagebox.INFORMATION);
        }
	}

	/*
	 * This event listener captures project dropping at root level. 
	 * We must first check all dropped elements are projects, calculations aren't allowed at this level.

	private class TreeDropEventListener implements EventListener<Event>{
		@Override
		public void onEvent(Event event) throws Exception {
			DropEvent drop = (DropEvent)event;
			Tree tree = (Tree)event.getTarget();
			Treeitem sourceItem = (Treeitem)drop.getDragged();
			Set<Object> sourceItems = ((CustomTreeModel)tree.getModel()).getSelection();			
			if(sourceItems.size() == 0){		//Not coming from multiple selection
				HashSet<Object> singleSourceItem = new HashSet<Object>();
				singleSourceItem.add(sourceItem.getAttribute("entity"));
				sourceItems = singleSourceItem;
			}
			//First check that all selected elements are projects
			for(Object item : sourceItems){				
				Entity dc;
				if(item instanceof TreeNode)
					dc = ((TreeNode<Entity>)item).getData();
				else
					dc = (Entity) item;
				if(dc.isCalculation()){
					Messagebox.show("Can't drop calculation/s on tree root, please select only projects");
					return;
				}
			}
			moveMultipleElementsToProject(buildRootProject(),  sourceItems);
		}
	}
	
	private Project buildRootProject() {
		Project project = new Project();
		project.setId(0);
		project.setName("");		
		project.setParentPath(Paths.getParent(Main.getUserPath()));
		project.setName(Paths.getTail(Main.getUserPath()));		
		return project;
	}

*/
}
