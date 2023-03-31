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
}
