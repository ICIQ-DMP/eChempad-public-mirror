/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.web.definitions;

/**
 * This class is used to substitute the usage of strings to mention the {@code Event} names with constant public fields
 * from this class. The number of {@code Event}s used in the application will be equal to the number of fields of
 * this class.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 16/2/2022
 * @see <a href="https://www.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model">...</a>
 */
public class EventNames {

    /**
     * Event that is used to notify the ItemDetailsComposer to render an item.
     */
    public static final String DISPLAY_ENTITY_EVENT = "DISPLAY_ENTITY_EVENT";

    /**
     * Event that is used to modify the properties from an entity in the tree and in the backend.
     */
    public static final String MODIFY_ENTITY_PROPERTIES_EVENT = "MODIFY_ENTITY_PROPERTIES_EVENT";

    /**
     * Event used to create a new entity as children under another in the tree, modifying the view and the backend.
     */
    public static final String CREATE_CHILDREN_WITH_PROPERTIES_EVENT = "CREATE_CHILDREN_WITH_PROPERTIES_EVENT";

    /**
     * Event to delete an entity from the tree using its id.
     */
    public static final String DELETE_TREE_ENTITY_EVENT = "DELETE_TREE_ENTITY_EVENT";

    /**
     * Event to clear the details of an entity in the details component.
     */
    public static final String CLEAR_ENTITY_DETAILS_EVENT = "CLEAR_ENTITY_DETAILS_EVENT";

    /**
     * Event that reloads the data from the backend. Attended in the tree.
     */
    public static final String REFRESH_EVENT = "REFRESH_EVENT";

    /**
     * Event that is generated in the toolbar and is sent to the tree so the tree can get the selected elements and send
     * them back into Dataverse.
     */
    public static final String EXPORT_SELECTED_ENTITY_EVENT = "EXPORT_SELECTED_ENTITY_EVENT";

    /**
     * Event that is generated in the tree and is sent to the display panel to display a new file.
     */
    public static final String DISPLAY_FILE_DISPLAY_PANEL_EVENT = "DISPLAY_FILE_DISPLAY_PANEL_EVENT";


}
