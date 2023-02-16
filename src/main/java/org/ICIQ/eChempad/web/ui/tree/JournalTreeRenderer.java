package org.ICIQ.eChempad.web.ui.tree;

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
public class JournalTreeRenderer implements TreeitemRenderer<DefaultTreeNode<Journal>> {
    public void render(Treeitem item, DefaultTreeNode<Journal> data, int index) throws Exception {
        Journal fi = data.getData();
        Treerow tr = new Treerow();
        item.appendChild(tr);
        tr.appendChild(new Treecell(fi.getName()));
        tr.appendChild(new Treecell(fi.getDescription()));
    }
}