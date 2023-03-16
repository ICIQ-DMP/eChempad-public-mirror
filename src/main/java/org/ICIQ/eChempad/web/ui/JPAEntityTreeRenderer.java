package org.ICIQ.eChempad.web.ui;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.zkoss.zul.*;

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
public class JPAEntityTreeRenderer implements TreeitemRenderer<DefaultTreeNode<JPAEntity>> {
    public void render(Treeitem item, DefaultTreeNode<JPAEntity> data, int index) throws Exception {
        JPAEntity fi = data.getData();
        Treerow tr = new Treerow();

        // Append name and description
        if (fi instanceof Journal)
        {
            tr.appendChild(new Treecell( ((Journal)fi).getName()));
            tr.appendChild(new Treecell( ((Journal)fi).getDescription()));
        }
        else if (fi instanceof Experiment)
        {
            tr.appendChild(new Treecell( ((Experiment)fi).getName()));
            tr.appendChild(new Treecell( ((Experiment)fi).getDescription()));
        }
        else if (fi instanceof Document)
        {
            tr.appendChild(new Treecell( ((Document)fi).getName()));
            tr.appendChild(new Treecell( ((Document)fi).getDescription()));
        }

        // Append item type
        tr.appendChild(new Treecell(fi.getClass().getSimpleName()));

        // Append the date
        if (fi instanceof Journal)
        {
            tr.appendChild(new Treecell( ((Journal)fi).getCreationDate().toString()));
        }
        else if (fi instanceof Experiment)
        {
            tr.appendChild(new Treecell( ((Experiment)fi).getCreationDate().toString()));
        }
        else if (fi instanceof Document)
        {
            tr.appendChild(new Treecell( ((Document)fi).getCreationDate().toString()));
        }

        item.appendChild(tr);
    }
}