/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
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
package org.ICIQ.eChempad.web.composers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.services.DataverseExportService;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.EntityConversionService;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.ICIQ.eChempad.web.renderers.JPAEntityTreeRenderer;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
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
     * <p>
     * TODO Services do not enforce any type of security, only controllers do but they are expected to be called from
     * a HTTP REST call. Develop a new layer of controllers that can be called programmatically and enforce security or
     * try to make work the controller layer that is already implemented programmatically.
     */
    @WireVariable("containerService")
    private ContainerService<Container, UUID> containerService;

    @WireVariable("documentService")
    private DocumentService<Document, UUID> documentService;

    @WireVariable("entityConversionService")
    private EntityConversionService entityConversionService;

    /**
     * Service to export eChempad entities into Dataverse
     */
    @WireVariable("dataverseExportService")
    private DataverseExportService dataverseExportService;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        // Propagate call to parent
        super.doAfterCompose(comp);

        // this.createJournal();  // For testing

        // Initialize queues
        this.initActionQueues();

        // Sets the data to the tree
        this.refreshModel();
        // Sets how we want to display the data (look JPAEntityTreeRenderer)
        this.tree.setItemRenderer(new JPAEntityTreeRenderer());
    }

    /**
     * Creates an example journal programmatically with experiments and documents in the database.
     */
    public void createJournal() {
        Container exampleContainer = new Container("Journal testing", "This is description");

        Document exampleDocument1 = new Document("File Nested in two levels", "with description very nested");
        exampleDocument1.setContentType(MediaType.ALL);
        Set<Document> exampleDocuments = new HashSet<Document>();
        exampleDocuments.add(exampleDocument1);

        this.containerService.save(exampleContainer);
        this.documentService.save(exampleDocument1);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initActionQueues() {
        this.treeQueue = EventQueues.lookup(EventQueueNames.TREE_QUEUE, EventQueues.DESKTOP, true);
        this.itemDetailsQueue = EventQueues.lookup(EventQueueNames.ITEM_DETAILS_QUEUE, EventQueues.DESKTOP, true);
        this.treeQueue.subscribe((EventListener) event -> {
            switch (event.getName()) {
                case EventNames.MODIFY_ENTITY_PROPERTIES_EVENT: {
                    this.unParseOverTreeCell((Entity) event.getData(), this.tree.getSelectedItem());
                    break;
                }
                case EventNames.CREATE_CHILDREN_WITH_PROPERTIES_EVENT: {
                    // Check if there is a selected element. If not just create it in the root.
                    Entity entity = null;
                    Treeitem selectedItem = this.tree.getSelectedItem();
                    DefaultTreeNode newNode = new DefaultTreeNode(event.getData());
                    if (selectedItem == null) {
                        // There is no selection, create the new project as a Journal in the root
                        DefaultTreeNode selectedNode = (DefaultTreeNode) this.tree.getModel().getRoot();
                        selectedNode.add(newNode);
                    } else  // There is a selection. We need to create element under the selection.
                    {
                        // Check the type of the selection
                        for (Component child : selectedItem.getTreerow().getChildren()) {
                            if (((Treecell) child).getTreecol().getId().equals("Type")) {
                                switch (((Treecell) child).getLabel()) {
                                    case "Document": {
                                        Logger.getGlobal().warning("cannot add entity to a document");
                                        throw new Exception();
                                    }
                                    case "Experiment": {
                                        entity = this.entityConversionService.parseDocument((DataEntity) event.getData());
                                        break;
                                    }
                                    case "Journal": {
                                        //entity = this.entityConversionService.parseExperiment((Entity) event.getData());
                                        break;
                                    }
                                    default: {
                                        Logger.getGlobal().warning(((Treecell) child).getLabel() + " is not a recognised type ");
                                    }
                                }
                            }
                        }

                        // Create a children and set the selected item as parent.
                        Treechildren newChild = new Treechildren();
                        newChild.setParent(selectedItem);

                        // Create a new item, attach received data to it, and set the new child as parent
                        Treeitem newItem = new Treeitem();
                        newItem.setValue(new DefaultTreeNode<Entity>(entity));
                        newItem.setParent(newChild);

                        // Finally, close the loop by adding the new Child as child of the selected item
                        // selectedItem.getChildren().add(newChild);
                    }
                }
                case EventNames.DELETE_TREE_ENTITY_EVENT: {
                    // UI modification tree
                    Treeitem selectedItem = this.tree.getSelectedItem();
                    if (selectedItem != null) {
                        selectedItem.detach();  // Remove the selected Treeitem
                    }

                    // UI modification item details
                    this.itemDetailsQueue.publish(new Event(EventNames.CLEAR_ENTITY_DETAILS_EVENT, this.tree, null));

                    // Backend modification
                    this.removeBackendEntity((Entity) event.getData());

                    break;
                }
                case EventNames.REFRESH_EVENT: {
                    this.refreshModel();

                    break;
                }
                case EventNames.EXPORT_SELECTED_ENTITY_EVENT: {
                    // Check if there is selection
                    Treeitem selectedItem = this.tree.getSelectedItem();
                    if (selectedItem == null) {
                        throw new Exception();
                    }

                    UUID journalToExport = null;
                    // If there is selection check that the selection is of Journal type
                    for (Component child : selectedItem.getTreerow().getChildren()) {
                        if (((Treecell) child).getTreecol().getId().equals("Type")) {
                            if (!((Treecell) child).getTreecol().getLabel().equals("Journal")) {
                                // Not a journal selected
                                throw new Exception();
                            }
                        }
                    }

                    UUID uuidSelected = null;
                    // Get UUID of the selected item, which we know that is a Journal
                    for (Component child : selectedItem.getTreerow().getChildren()) {
                        if (((Treecell) child).getTreecol().getId().equals("hiddenID")) {
                            uuidSelected = UUID.fromString(((Treecell) child).getLabel());
                        }
                    }

                    try {
                        // Call backend service to export the selected Journal by using its UUID.
                        this.dataverseExportService.exportJournal(uuidSelected);
                    } catch (ResourceAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        });

    }

    /**
     * Receives an object instance that contains the data to be applied over the selected item of the tree.
     *
     * @param entity Entity that contains data.
     */
    public void unParseOverTreeCell(Entity entity, Treeitem treeitem) {
        // Obtain the childrenComponents of the received TreeItem.
        Treerow treerow = treeitem.getTreerow();
        List<Treecell> childrenComponents = treerow.getChildren();

        // Parse tree cells into fields of the entity using id to identify
        for (Treecell treecell : childrenComponents) {
            Treecol treecol = treecell.getTreecol();
            String id = treecol.getId();
            switch (id) {
                case "descriptionTreeColumn": {
                    if (entity instanceof DataEntity) {
                        treecell.setLabel(((DataEntity) entity).getDescription());
                    } else {
                        treecell.setLabel("");
                    }
                    break;
                }
                case "nameTreeColumn": {
                    if (entity instanceof DataEntity) {
                        treecell.setLabel(((DataEntity) entity).getName());
                    } else {
                        treecell.setLabel("");
                    }
                    break;
                }
                case "creationDateTreeColumn": {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat();

                    Logger.getGlobal().warning(entity.getCreationDate().toString());
                    Logger.getGlobal().warning(dateFormatter.format(entity.getCreationDate()));

                    treecell.setLabel(dateFormatter.format(entity.getCreationDate()));
                    break;
                }
                case "hiddenID": {
                    treecell.setLabel(entity.getId().toString());
                    break;
                }
                default: {
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
    public Treeitem getEmptyCustomTreeItem() {
        Treeitem newTreeItem = new Treeitem();
        Treerow newTreeRow = new Treerow();

        for (Component treecol : this.tree.getTreecols().getChildren()) {
            Logger.getGlobal().warning("treecol id " + treecol.getId() + " treecol value " + ((Treecol) treecol).getLabel());
            String id = treecol.getId();
            switch (id) {
                case "descriptionTreeColumn": {

                    Treecell descriptionTreeColumn = new Treecell();
                    descriptionTreeColumn.setId("descriptionTreeCell");
                    newTreeRow.appendChild(descriptionTreeColumn);
                    break;
                }
                case "nameTreeColumn": {
                    Treecell nameTreeColumn = new Treecell();
                    nameTreeColumn.setId("nameTreeCell");
                    newTreeRow.appendChild(nameTreeColumn);
                    break;
                }
                case "creationDateTreeColumn": {
                    Treecell creationDateTreeColumn = new Treecell();
                    creationDateTreeColumn.setId("creationDateTreeCell");
                    newTreeRow.appendChild(creationDateTreeColumn);
                    break;
                }
                case "hiddenID": {
                    Treecell hiddenID = new Treecell();
                    hiddenID.setId("hiddenIDCell");
                    newTreeRow.appendChild(hiddenID);
                    break;
                }
                default: {
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
     * @param entity Entity that is going to be removed from the DB.
     */
    public void removeBackendEntity(Entity entity) {
        switch (entity.getTypeName()) {
            case "org.ICIQ.eChempad.entities.genericJPAEntities.Container": {
                this.containerService.delete((Container) entity);
                break;
            }
            case "org.ICIQ.eChempad.entities.genericJPAEntities.Document": {
                this.documentService.delete((Document) entity);
                break;
            }
            default: {
                Logger.getGlobal().warning("This type is not recognised as entity type: " + entity.getTypeName());
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
    public DefaultTreeModel<Entity> createModel() {

        List<DefaultTreeNode<Entity>> journalNodesList = new ArrayList<DefaultTreeNode<Entity>>();
        List<Container> userContainers = this.containerService.findAll();
        for (Container container : userContainers) {
            // If this container is not a root container, skip
            if (container.getParent() != null) {
                continue;
            }

            // For each journal loop through all the experiments
            List<Container> experimentList = new ArrayList<>(container.getChildrenContainers());
            DefaultTreeNode<Entity>[] experimentNodes = (DefaultTreeNode<Entity>[]) Array.newInstance(DefaultTreeNode.class, experimentList.size());
            for (int i = 0; i < experimentList.size(); i++) {
                Container experiment = experimentList.get(i);
                List<Document> documentList = new ArrayList<>(experiment.getChildrenDocuments());
                DefaultTreeNode<Entity>[] documentNodes = (DefaultTreeNode<Entity>[]) Array.newInstance(DefaultTreeNode.class, documentList.size());
                for (int j = 0; j < documentList.size(); j++) {
                    documentNodes[j] = new DefaultTreeNode<Entity>(documentList.get(j));
                }

                experimentNodes[i] = new DefaultTreeNode<Entity>(experimentList.get(i), documentNodes);
            }
            journalNodesList.add(new DefaultTreeNode<Entity>(container, experimentNodes));
        }

        // Create the Array of TreeNode
        DefaultTreeNode<Entity>[] journalNodes = (DefaultTreeNode<Entity>[]) Array.newInstance(DefaultTreeNode.class, journalNodesList.size());
        for (int i = 0; i < journalNodesList.size(); i++) {
            journalNodes[i] = journalNodesList.get(i);
        }

        return new DefaultTreeModel<Entity>(new DefaultTreeNode<Entity>(null, journalNodes));
    }

    /**
     * Every time a tab from the tree is clicked, we send a message to the navigation queue in order to update the
     * details display.
     *
     * @param event Contains the data of the event.
     */
    @Listen("onClick = #tree")
    public void treeTabClick(MouseEvent event) {
        // Obtain the TreeItem that was selected.
        Treeitem selectedItem = ((Tree) event.getTarget()).getSelectedItem();

        // If selection is null do nothing
        if (selectedItem == null) {
            return;
        }

        // Get the children components from the selected Treerow
        Entity entity = this.parseEntityFromTreeItem(selectedItem);

        this.itemDetailsQueue.publish(new Event(EventNames.DISPLAY_ENTITY_EVENT, this.treeWindow, entity));
    }

    /**
     * Resets the tree model
     */
    public void refreshModel() {
        this.tree.setModel(this.createModel());
    }

    public void refreshAllItems() {
        List<Component> components = this.tree.getChildren();
        for (Component component : components) {
            Logger.getGlobal().warning("invalidating " + component);
            component.invalidate();
        }
    }

    // Data transformation methods

    /**
     * Receives a {@code Treeitem} object from the tree and parses the data inside into the corresponding JPAEntity.
     *
     * @param treeitem An item from the tree
     * @return An object that contains the values of the {@code Treeitem}.
     */
    public Entity parseEntityFromTreeItem(Treeitem treeitem) {
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
        DataEntity entity = null;
        try {
            entity = (DataEntity) Objects.requireNonNull(entityClass).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (entity == null) {
            return null;
        }

        // Parse tree cells into fields of the entity using id to identify
        for (Treecell treecell : childrenComponents) {
            String id = treecell.getTreecol().getId();
            String content = treecell.getLabel();

            switch (id) {
                case "descriptionTreeColumn": {
                    entity.setDescription(content);
                    break;
                }
                case "nameTreeColumn": {
                    entity.setName(content);
                    break;
                }
                case "creationDateTreeColumn": {
                    try {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat();
                        entity.setCreationDate(dateFormatter.parse(content));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "hiddenID": {
                    entity.setId(UUID.fromString(content));
                    break;
                }
                case "typeTreeColumn": {
                    // Do nothing. The type is handled implicitly when choosing the entity
                    break;
                }
                default: {
                    Logger.getGlobal().warning("not recognised case: " + id);
                }
            }
        }

        return entity;
    }
}