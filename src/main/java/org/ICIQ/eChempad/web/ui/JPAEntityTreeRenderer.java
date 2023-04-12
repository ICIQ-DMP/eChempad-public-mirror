package org.ICIQ.eChempad.web.ui;

import net.bytebuddy.build.Plugin;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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
public class JPAEntityTreeRenderer implements TreeitemRenderer<DefaultTreeNode<JPAEntity>> {
    public void render(Treeitem item, DefaultTreeNode<JPAEntity> data, int index) throws Exception {
        JPAEntity fi = data.getData();
        Treerow tr = new Treerow();

        // Append name
        tr.appendChild(new Treecell(fi.getName()));

        // Append description
        tr.appendChild(new Treecell(fi.getDescription()));

        // Append item type
        tr.appendChild(new Treecell(fi.getClass().getSimpleName()));

        // Append the date
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        tr.appendChild(new Treecell(dateFormatter.format(fi.getCreationDate())));

        // Append hidden ID
        tr.appendChild(new Treecell(fi.getId().toString()));

        item.appendChild(tr);
    }
}