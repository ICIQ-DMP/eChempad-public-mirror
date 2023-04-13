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

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.EntityConversionService;
import org.ICIQ.eChempad.services.genericJPAServices.ExperimentService;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.ICIQ.eChempad.web.ui.JPAEntityTreeRenderer;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TreeComposer extends SelectorComposer<Window> {

    // Sending event queues
    /**
     * To send the events to the item details component in order to modify it.
     */
    private EventQueue<Event> itemDetailsQueue;

    // Receiving event queues
    /**
     * Events that the tree component has to attend.
     */
    private EventQueue<Event> treeQueue;


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
    @WireVariable("journalService")
    private JournalService<Journal, UUID> journalService;

    @WireVariable("experimentService")
    private ExperimentService<Experiment, UUID> experimentService;

    @WireVariable("documentService")
    private DocumentService<Document, UUID> documentService;

    @WireVariable("entityConversionService")
    private EntityConversionService entityConversionService;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        // Propagate call to parent
        super.doAfterCompose(comp);

        // this.createJournal();  // For testing

        // Initialize queues
        this.initActionQueues();

        // Sets the data to the tree
        this.tree.setModel(this.createModel());
        // Sets how we want to display the data (look JPAEntityTreeRenderer)
        this.tree.setItemRenderer(new JPAEntityTreeRenderer());
    }

    /**
     * Creates an example journal programmatically with experiments and documents in the database.
     */
    public void createJournal()
    {
        Journal exampleJournal = new Journal("Journal testing", "This is description");

        Experiment exampleExperiment1 = new Experiment("Experiment This is supposed to be nested", "this even more nested because of files !");
        Experiment exampleExperiment2 = new Experiment("Experiment Nested nested", "this too");
        exampleExperiment1.setJournal(exampleJournal);
        exampleExperiment2.setJournal(exampleJournal);

        Document exampleDocument1 = new Document("File Nested in two levels", "with description very nested");
        exampleDocument1.setContentType(MediaType.ALL);
        exampleDocument1.setExperiment(exampleExperiment1);
        Set<Document> exampleDocuments = new HashSet<Document>();
        exampleDocuments.add(exampleDocument1);

        exampleExperiment1.setDocuments(exampleDocuments);
        Set<Experiment> exampleExperiments = new HashSet<Experiment>();
        exampleExperiments.add(exampleExperiment1);
        exampleExperiments.add(exampleExperiment2);

        exampleJournal.setExperiments(exampleExperiments);

        this.journalService.save(exampleJournal);
        this.experimentService.save(exampleExperiment1);
        this.experimentService.save(exampleExperiment2);
        this.documentService.save(exampleDocument1);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initActionQueues(){
        this.treeQueue = EventQueues.lookup(EventQueueNames.TREE_QUEUE, EventQueues.DESKTOP, true);
        this.itemDetailsQueue = EventQueues.lookup(EventQueueNames.ITEM_DETAILS_QUEUE, EventQueues.DESKTOP, true);
        this.treeQueue.subscribe((EventListener) event -> {
            switch(event.getName()) {
                case EventNames.MODIFY_ENTITY_PROPERTIES_EVENT:
                {
                    this.unParseOverTreeCell((JPAEntity) event.getData(), this.tree.getSelectedItem());
                    break;
                }
                case EventNames.CREATE_CHILDREN_WITH_PROPERTIES_EVENT:
                {
                    // Check if there is a selected element. If not just create it in the root.
                    Treeitem selectedItem = this.tree.getSelectedItem();
                    if (selectedItem == null)
                    {
                        // There is no selection, create the new project as a Journal in the root
                        Treechildren root = tree.getTreechildren();  // Get the Treechildren object of the tree
                        if (root == null)
                        {
                            root = new Treechildren();
                            tree.appendChild(root);  // Append the Treechildren object to the tree
                        }

                        // Obtain a template treeitem and append it to the tree
                        Treeitem newTreeItem = this.getEmptyCustomTreeItem();
                        root.appendChild(newTreeItem);

                        this.unParseOverTreeCell((JPAEntity) event.getData(), newTreeItem);
                    }
                    else  // There is a selection. We need to create element under the selection.
                    {
                        // Ignore new entities created under a Document
                        if (((JPAEntity) event.getData()).getTypeName().equals("Document"))
                        {
                            Logger.getGlobal().warning("cannot create children under document entity");
                            break;
                        }

                        // Obtain children from the selected element. If not available create it and append to the
                        // selection
                        Treechildren treechildren = selectedItem.getTreechildren();
                        if (treechildren == null)
                        {
                            treechildren = new Treechildren();  // Create a list of children under the selected element
                            selectedItem.appendChild(treechildren);
                        }

                        // Create the tree item that will store the newly created info
                        Treeitem newTreeItem = new Treeitem();
                        treechildren.appendChild(newTreeItem);

                        // Obtain the type of the selected element
                        Optional<Component> typeTreeCellOptional = selectedItem.getTreerow().getChildren().stream().filter(
                                (Component comp) -> {
                                    return ((Treecell) comp).getTreecol().getId().equals("Type");
                                }
                        )
                                .findFirst();

                        String selectedType;
                        if (typeTreeCellOptional.isPresent())
                        {
                            selectedType = ((Treecell) typeTreeCellOptional.get()).getTreecol().getLabel();
                        }
                        else throw new Exception();

                        // Parse item details sent entity into the desired type depending on the type of the selected
                        // item
                        JPAEntity newJpaEntity;
                        if (selectedType.equals("Journal"))
                        {
                            newJpaEntity = this.entityConversionService.parseExperiment((JPAEntity) event.getData());
                        }
                        else if (selectedType.equals("Experiment"))
                        {
                            newJpaEntity = this.entityConversionService.parseExperiment((JPAEntity) event.getData());
                        }

                        // Unparse data over the tree item
                        this.unParseOverTreeCell((JPAEntity) event.getData(), newTreeItem);
                    }
                }
                case EventNames.DELETE_TREE_ENTITY_EVENT:
                {
                    // UI modification tree
                    Treeitem selectedItem = this.tree.getSelectedItem();
                    if (selectedItem != null) {
                        selectedItem.detach();  // Remove the selected Treeitem
                    }

                    // UI modification item details
                    this.itemDetailsQueue.publish(new Event(EventNames.CLEAR_ENTITY_DETAILS_EVENT, this.tree, null));

                    // Backend modification
                    this.removeBackendEntity((JPAEntity) event.getData());

                    break;
                }
                default:
                {
                    break;
                }
            }
        });

    }

    /**
     * Receives an object instance that contains the data to be applied over the selected item of the tree.
     *
     * @param jpaEntity Entity that contains data.
     */
    public void unParseOverTreeCell(JPAEntity jpaEntity, Treeitem treeitem)
    {
        // Obtain the childrenComponents of the received TreeItem.
        Treerow treerow = treeitem.getTreerow();
        List<Treecell> childrenComponents = treerow.getChildren();

        // Parse tree cells into fields of the entity using id to identify
        for (Treecell treecell : childrenComponents)
        {
            Treecol treecol = treecell.getTreecol();
            String id = treecol.getId();
            switch (id)
            {
                case "descriptionTreeColumn":
                {
                    treecell.setLabel(jpaEntity.getDescription());
                    break;
                }
                case "nameTreeColumn":
                {
                    treecell.setLabel(jpaEntity.getName());
                    break;
                }
                case "creationDateTreeColumn":
                {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat();

                    Logger.getGlobal().warning(jpaEntity.getCreationDate().toString());
                    Logger.getGlobal().warning(dateFormatter.format(jpaEntity.getCreationDate()));

                    treecell.setLabel(dateFormatter.format(jpaEntity.getCreationDate()));
                    break;
                }
                case "hiddenID":
                {
                    treecell.setLabel(jpaEntity.getId().toString());
                    break;
                }
                default:
                {
                    Logger.getGlobal().warning("not recognised case" + id);
                }
            }
        }
    }


    /**
     * This method returns an instance of Treeitem that contains the rows that we use on the Zkoss template.
     *
     * @return Treeitem instance with a row containing all the Treecells and the properties that we are using on the tree.
     */
    public Treeitem getEmptyCustomTreeItem()
    {
        Treeitem newTreeItem = new Treeitem();
        Treerow newTreeRow = new Treerow();

        for (Component treecol : this.tree.getTreecols().getChildren())
        {
            Logger.getGlobal().warning("treecol id " + treecol.getId() + " treecol value " + ((Treecol) treecol).getLabel());
            String id = treecol.getId();
            switch (id)
            {
                case "descriptionTreeColumn":
                {

                    Treecell descriptionTreeColumn = new Treecell();
                    descriptionTreeColumn.setId("descriptionTreeCell");
                    newTreeRow.appendChild(descriptionTreeColumn);
                    break;
                }
                case "nameTreeColumn":
                {
                    Treecell nameTreeColumn = new Treecell();
                    nameTreeColumn.setId("nameTreeCell");
                    newTreeRow.appendChild(nameTreeColumn);
                    break;
                }
                case "creationDateTreeColumn":
                {
                    Treecell creationDateTreeColumn = new Treecell();
                    creationDateTreeColumn.setId("creationDateTreeCell");
                    newTreeRow.appendChild(creationDateTreeColumn);
                    break;
                }
                case "hiddenID":
                {
                    Treecell hiddenID = new Treecell();
                    hiddenID.setId("hiddenIDCell");
                    newTreeRow.appendChild(hiddenID);
                    break;
                }
                default:
                {
                    Logger.getGlobal().warning("not recognised case" + id);
                    break;
                }
            }
        }

        newTreeItem.appendChild(newTreeRow);

        return newTreeItem;
    }


    /**
     * Method to produce changes in the backend of the application.
     *
     * @param jpaEntity Entity that is going to be removed from the DB.
     */
    public void removeBackendEntity(JPAEntity jpaEntity)
    {
        switch (jpaEntity.getTypeName())
        {
            case "org.ICIQ.eChempad.entities.genericJPAEntities.Journal":
            {
                this.journalService.delete((Journal) jpaEntity);
                break;
            }
            case "org.ICIQ.eChempad.entities.genericJPAEntities.Experiment":
            {
                this.experimentService.delete((Experiment) jpaEntity);
                break;
            }
            case "org.ICIQ.eChempad.entities.genericJPAEntities.Document":
            {
                this.documentService.delete((Document) jpaEntity);
                break;
            }
            default:
            {
                Logger.getGlobal().warning(jpaEntity.getTypeName());

                Logger.getGlobal().warning("Nope");
                break;
            }
        }
    }

    /**
     * Queries the database for all {@code Journal} from the logged user and builds a {@code TreeModel} from scratch. To
     * do so, transform each {@code Journal} in the Database to an array of {@code DefaultTreeNode}.
     *
     * @return All the journals and its content as a suitable datatype to work with ZK.
     */
    public DefaultTreeModel<JPAEntity> createModel()
    {
        List<DefaultTreeNode<JPAEntity>> journalNodesList = new ArrayList<DefaultTreeNode<JPAEntity>>();
        List<Journal> userJournals = this.journalService.findAll();
        for (Journal journal: userJournals) {
            // For each journal loop through all the experiments
            List<Experiment> experimentList = new ArrayList<>(journal.getExperiments());
            DefaultTreeNode<JPAEntity>[] experimentNodes = (DefaultTreeNode<JPAEntity>[]) Array.newInstance(DefaultTreeNode.class, experimentList.size());
            for (int i = 0; i < experimentList.size(); i++)
            {
                Experiment experiment = experimentList.get(i);
                List<Document> documentList = new ArrayList<>(experiment.getDocuments());
                DefaultTreeNode<JPAEntity>[] documentNodes = (DefaultTreeNode<JPAEntity>[]) Array.newInstance(DefaultTreeNode.class, documentList.size());
                for (int j = 0; j < documentList.size(); j++) {
                    documentNodes[j] = new DefaultTreeNode<JPAEntity>(documentList.get(j));
                }

                Logger.getGlobal().warning(experimentList.get(i).toString());
                experimentNodes[i] = new DefaultTreeNode<JPAEntity>(experimentList.get(i), documentNodes);
            }
            journalNodesList.add(new DefaultTreeNode<JPAEntity>(journal, experimentNodes));
        }

        // Create the Array of TreeNode
        DefaultTreeNode<JPAEntity>[] journalNodes = (DefaultTreeNode<JPAEntity>[]) Array.newInstance(DefaultTreeNode.class, journalNodesList.size());
        for (int i = 0; i < journalNodesList.size(); i++)
        {
            journalNodes[i] = journalNodesList.get(i);
        }

        return new DefaultTreeModel<JPAEntity>(new DefaultTreeNode<JPAEntity>(null, journalNodes));
    }

    /**
     * Every time a tab from the tree is clicked, we send a message to the navigation queue in order to update the
     * details display.
     *
     * @param event Contains the data of the event.
     */
    @Listen("onClick = #tree")
    public void treeTabClick(MouseEvent event){
        // Obtain the TreeItem that was selected.
        Treeitem selectedItem = ((Tree) event.getTarget()).getSelectedItem();

        // If selection is null do nothing
        if (selectedItem == null)
        {
            return;
        }

        // Get the children components from the selected Treerow
        JPAEntity entity = this.parseEntityFromTreeItem(selectedItem);

        this.itemDetailsQueue.publish(new Event(EventNames.DISPLAY_ENTITY_EVENT, this.treeWindow, entity));
    }

    /**
     * Handles the click on the refresh button of the tree component
     */
    @Listen("onClick=#refreshBtn")
	public void onRefreshBtnClick() {
        this.createModel();
	}

    // Data transformation methods

    /**
     * Receives a {@code Treeitem} object from the tree and parses the data inside into the corresponding JPAEntity.
     *
     * @param treeitem An item from the tree
     * @return An object that contains the values of the {@code Treeitem}.
     */
    public JPAEntity parseEntityFromTreeItem(Treeitem treeitem)
    {
        // Search the type of the entity received in the parameter and create its corresponding class.
        Class<?> entityClass = null;
        List<Treecell> childrenComponents = treeitem.getTreerow().getChildren();
        try {
            entityClass = Class.forName("org.ICIQ.eChempad.entities.genericJPAEntities." + Objects.requireNonNull(childrenComponents.stream()
                            .filter((Treecell treecell) ->
                            {
                                return treecell.getTreecol().getId().equals("typeTreeColumn");
                            })
                            .findFirst()
                            .get()
                            .getLabel()));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Create new instance from class using reflective paradigm.
        JPAEntity entity = null;
        try {
             entity = (JPAEntity) Objects.requireNonNull(entityClass).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (entity == null)
        {
            return null;
        }

        // Parse tree cells into fields of the entity using id to identify
        for (Treecell treecell : childrenComponents)
        {
            String id = treecell.getTreecol().getId();
            String content = treecell.getLabel();

            switch (id)
            {
                case "descriptionTreeColumn":
                {
                    entity.setDescription(content);
                    break;
                }
                case "nameTreeColumn":
                {
                    entity.setName(content);
                    break;
                }
                case "creationDateTreeColumn":
                {
                    try {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat();
                        entity.setCreationDate(dateFormatter.parse(content));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "hiddenID":
                {
                    entity.setId(UUID.fromString(content));
                    break;
                }
                default:
                {
                    Logger.getGlobal().warning("not recognised case");
                }
            }
        }

        return entity;
    }
















































    /*
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
	



*/


        /*
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
        }); */
	//}

/*

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
