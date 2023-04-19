/*
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

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
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
 * This class controls the events and manages the UI component of the item details, which is a panel with many text
 * boxes and labels to display information of the elements selected from the tree.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1.0
 */
public class ItemDetailsComposer extends SelectorComposer<Window> {

	/**
	 * Receiving event queue
	 */
	private EventQueue<Event> itemDetailsQueue = null;

	/**
	 * Events that the tree component has to attend.
	 */
	private EventQueue<Event> treeQueue;


	@Wire
	private Div itemDetailsDiv;

	@Wire
	private Label hiddenID;

	@Wire
	private Label currentSelectionTitle;

	@Wire
	private Textbox name;

	@Wire
	private Textbox description;

	@Wire
	private Label type;

	@Wire
	private Textbox path;

	@Wire
	private Listbox owner;

	@Wire
	private Listbox group;

	@Wire
	private Listbox permissions;

	@Wire
	private Textbox cDate;

	@Wire
	private Textbox mDate;

	@Wire
	private Textbox pDate;

	@Wire
	private Textbox conceptGroup;
		
	@Wire
	private Div operationButtonsLayout;

	@Wire
	private Button itemDetailsCreateButton;

	@Wire
	private Button itemDetailsModifyButton;

	@Wire
	private Button itemDetailsRemoveButton;


	// UI COMPOSER METHODS
	/**
	 * De-facto constructor for composer components.
	 *
	 * @param comp Window component
	 * @throws Exception If something goes wrong during initialization.
	 */
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		// Create event queues
		this.initActionQueues();

		// Set UI initial state
		this.setInitialDetailsState();
	}

	/**
	 * This method initializes the item details form so the create button could be pressed right away after the
	 * initialization fo the UI. It assigns a type Journal, a creation date in the moment of initialization and a UUID
	 * so a new Journal can be created.
	 */
	private void setInitialDetailsState()
	{
		// Enable create button from the beginning
		this.itemDetailsCreateButton.setDisabled(false);
		this.itemDetailsModifyButton.setDisabled(true);
		this.itemDetailsRemoveButton.setDisabled(true);

		// Assign default data to the item details component so the creation button has consistent data at startup
		this.type.setValue("Journal");
		this.cDate.setValue(new SimpleDateFormat().format(new Date()));
		this.hiddenID.setValue(UUID.randomUUID().toString());
	}

	/**
	 * Initialize the action queues used to receive events and also declared the event listeners to attend to events.
	 */
	private void initActionQueues(){
		this.itemDetailsQueue = EventQueues.lookup(EventQueueNames.ITEM_DETAILS_QUEUE, EventQueues.DESKTOP, true);
		this.itemDetailsQueue.subscribe(event -> {
			switch (event.getName()) {
				case EventNames.DISPLAY_ENTITY_EVENT:
				{
					this.displayEntityDetails((JPAEntity) event.getData());
					break;
				}
				case EventNames.CLEAR_ENTITY_DETAILS_EVENT:
				{
					this.clearDetails();
					break;
				}
				default:
				{
					Logger.getGlobal().warning("not recognized event");
				}
			}
		});

		this.treeQueue = EventQueues.lookup(EventQueueNames.TREE_QUEUE, EventQueues.DESKTOP, true);
	}

	/**
	 * Displays the details of an entity in the ItemDetails component.
	 *
	 * @param entity Entity that contains the data to represent in the UI.
	 */
	public void displayEntityDetails(JPAEntity entity)
	{
		// Load data into UI
		this.hiddenID.setValue(entity.getId().toString());
		this.name.setValue(entity.getName());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		this.cDate.setValue(simpleDateFormat.format(entity.getCreationDate()));
		this.description.setValue(entity.getDescription());
		this.type.setValue(entity.getClass().getSimpleName());


		// Enable delete and modification button
		this.itemDetailsRemoveButton.setDisabled(false);
		this.itemDetailsModifyButton.setDisabled(false);
	}

	/**
	 * Clears all the details from the ItemDetails UI component.
	 */
	public void clearDetails()
	{
		// Load data into UI
		this.hiddenID.setValue(UUID.randomUUID().toString());
		this.name.setValue("");
		this.cDate.setValue(new SimpleDateFormat().format(new Date()));
		this.description.setValue("");
		this.type.setValue("Journal");

		// UI comp not used yet
		this.conceptGroup.setText("");
		this.path.setText("");
		this.mDate.setText("");
		this.pDate.setText("");
		this.permissions.setSelectedIndex(0);

		// Enable delete and modification button
		this.itemDetailsRemoveButton.setDisabled(true);
		this.itemDetailsModifyButton.setDisabled(true);
		this.itemDetailsCreateButton.setDisabled(false);
	}


	// LISTENER METHODS

	/**
	 * Parses entity from details and send event to the tree component to create entity.
	 *
	 * @throws Exception If something goes wrong
	 */
	@Listen("onClick = #itemDetailsCreateButton")
	public void createProjectClick() throws Exception{
		this.treeQueue.publish(new Event(EventNames.CREATE_CHILDREN_WITH_PROPERTIES_EVENT, null, this.parseEntityFromDetails()));
	}

	/**
	 * Parses entity from details and send event to the tree component to modify the selected element.
	 *
	 * @throws InterruptedException If the process is interrupted via UI.
	 * @throws Exception If something goes wrong.
	 */
	@Listen("onClick = #itemDetailsModifyButton")
	public void modifyClick() throws InterruptedException, Exception{
		this.treeQueue.publish(new Event(EventNames.MODIFY_ENTITY_PROPERTIES_EVENT, null, this.parseEntityFromDetails()));
	}

	/**
	 * Parses entity from details and send event to the tree component to delete the selected element.
	 *
	 * @throws InterruptedException If the process is interrupted via UI.
	 */
	@Listen("onClick = #itemDetailsRemoveButton")
	public void removeClick() throws InterruptedException{
		this.treeQueue.publish(new Event(EventNames.DELETE_TREE_ENTITY_EVENT, null, this.parseEntityFromDetails()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void deleteSelectedElement() throws InterruptedException{
		/*if (getSelectedElementPath() == null)
			Messagebox.show("No selected Element, please, select one.","No selection",Messagebox.OK, Messagebox.ERROR);
		else
			Messagebox.show("Confirm element removal: \n" + getSelectedElementPath() ,"Remove element",Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new DeleteElementListener());*/
	}

	private JPAEntity parseEntityFromDetails()
	{
		// Search the type of the entity received in the parameter and create its corresponding class.
		Class<?> entityClass = null;
		try {
			entityClass = Class.forName("org.ICIQ.eChempad.entities.genericJPAEntities." + this.type.getValue());
		} catch (ClassNotFoundException e) {
			entityClass = Journal.class;
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

		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		try {
			Logger.getGlobal().warning("itemdetails value " + this.cDate.getValue());
			entity.setCreationDate(dateFormatter.parse(this.cDate.getValue()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		entity.setName(this.name.getValue());
		entity.setDescription(this.description.getValue());

		Logger.getGlobal().warning("this.hiddenID.getId()" + this.hiddenID.getValue());
		entity.setId(UUID.fromString(this.hiddenID.getValue()));

		return entity;
	}
}
