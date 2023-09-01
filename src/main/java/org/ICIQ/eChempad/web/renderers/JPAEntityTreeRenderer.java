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
package org.ICIQ.eChempad.web.renderers;

import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * This class is used to provide an implementation to the render method, which receives data from the tree as Journal
 * instances and translate them to changes in the tree of the ZK web UI that render the Journal.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 16/2/2022
 * @see <a href="https://www.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model">...</a>
 */
public class JPAEntityTreeRenderer implements TreeitemRenderer<DefaultTreeNode<Entity>> {
    public void render(Treeitem item, DefaultTreeNode<Entity> data, int index) throws Exception {
        if (data == null)
        {
            Logger.getGlobal().warning("null data in renderer");
            return;
        }
        DataEntity fi = (DataEntity) data.getData();
        Treerow tr = new Treerow();

        // Append name
        tr.appendChild(new Treecell(fi.getName()));

        // Append description
        tr.appendChild(new Treecell(fi.getDescription()));

        // Append item type
        if (fi.getClass().getSimpleName().equals("Container"))
        {
            tr.appendChild(new Treecell("Container"));
        }
        else if (fi.getOriginType() != null)
        {
            tr.appendChild(new Treecell("Document (" + fi.getOriginType() + ")"));
        }
        else
        {
            tr.appendChild(new Treecell("Document "));
        }

        // Append the date
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        tr.appendChild(new Treecell(dateFormatter.format(fi.getCreationDate())));

        // Append hidden ID
        tr.appendChild(new Treecell(fi.getId().toString()));

        item.appendChild(tr);
    }
}