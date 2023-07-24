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
 * This class is used to substitute the usage of strings to mention the {@code EventQueue}s with constant public fields
 * from this class. The number of {@code EventQueue}s used in the application will be equal to the number of fields of
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
public class EventQueueNames {

    /**
     * Queue used by the item details component, which will receive events to rewrite the UI when elements from the tree
     * are selected.
     */
    public static final String ITEM_DETAILS_QUEUE = "ITEM_DETAILS_QUEUE";

    /**
     * Queue used by the tree component, which will receive events to modify or create elements from the tree and also
     * modify accordingly the backend.
     */
    public static final String TREE_QUEUE = "TREE_QUEUE";
}
