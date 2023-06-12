package org.ICIQ.eChempad.entities.genericJPAEntities;

/**
 * This interface extends the contract that a model class needs to fulfill in order to properly model an entity of the
 * database that is also used to store information.
 * <p>
 * This interface provides common methods that data entities in the database have. These methods usually suggest the
 * existence of certain data fields in the implementing classes.
 */
public interface DataEntity extends Entity{

    /**
     * Returns a name for this data entity.
     * @return name of the current data entity.
     */
    default String getName()
    {
        return null;
    }

    /**
     * Sets the name of this data entity.
     * @param name New name of the current data entity.
     */
    default void setName(String name){}

    /**
     * Returns the description for this data entity.
     * @return Description of the current data entity.
     */
    default String getDescription()
    {
        return null;
    }

    /**
     * Sets the name of this data entity.
     * @param description New description of the current data entity.
     */
    default void setDescription(String description){}

    /**
     * Gets the parent entity of the current entity.
     *
     * @return the parent entity.
     */
    default Container getParent()
    {
        return null;
    }

    /**
     * Sets the parent entity of the current entity.
     *
     * @param parent the new parent entity.
     */
    default void setParent(Container parent){}
}
