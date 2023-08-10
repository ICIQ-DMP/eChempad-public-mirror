package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.util.Date;

@jakarta.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public abstract class DataEntityImpl extends EntityImpl implements DataEntity{

    /**
     * Reference to the {@code Container} that this {@code DataEntity} is in. It could be null, that would mean that
     * this {@code DataEntity} is not in any {@code Container}, which means that this would be a root container.
     */
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "parent_id"
    )
    @JsonIgnore
    protected Container parent;

    /**
     * Name of this {@code DataEntity}.
     */
    @Column(length = 1000, nullable = false)
    protected String name;

    /**
     * Description of this {@code DataEntity}.
     */
    @Column(length = 1000, nullable = false)
    protected String description;

    /**
     * Date of creation of the entity. Access with read only allow us to ignore the creation date field when doing post
     * (parsing JSON) but including it when we do gets.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Date creationDate;

    /**
     * Date of last edition of this Entity.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Date lastEditionDate;

    /**
     * Date of last edition of the data that this Entity corresponds to in the origin platform.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Date originLastEditionDate;

    /**
     * The String that identifies this DataEntity in the originPlatform. It could be encoded in many ways.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 1000)
    private String originId;

    /**
     * The String that identifies the platform that this DataEntity is coming from. It can be eChempad or Signals
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 1000)
    private String originPlatform;

    /**
     * The String that reflects the type of the original element. It can be Experiment, Journal, Document...
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 1000)
    private String originType;

    /**
     * The String that stores the digest value of the data that this entity comes from. It can be encoded differently
     * for different platforms.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 100)
    private String digest;

    /**
     * The String that stores the membership of this DataEntity. It is the surname of the group leader. Bo, Echavarren,
     * López...
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 100)
    private String department;

    /**
     * The String that stores the username of the user that created the data that this DataEntity corresponds to in the
     * origin platform.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 100)
    private String originOwnerUsername;

    public void initCreationDate() {
        this.creationDate = new Date();
    }

    @Override
    public void updateLastEditionDate() {
        this.lastEditionDate = new Date();
    }

    // SETTERS GETTERS AND TO STRINGS
    public Container getParent() {
        return parent;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastEditionDate() {
        return lastEditionDate;
    }

    @Override
    public void setLastEditionDate(Date lastEditionDate) {
        this.lastEditionDate = lastEditionDate;
    }

    @Override
    public Date getOriginLastEditionDate() {
        return originLastEditionDate;
    }

    @Override
    public void setOriginLastEditionDate(Date originLastEditionDate) {
        this.originLastEditionDate = originLastEditionDate;
    }

    @Override
    public String getOriginId() {
        return originId;
    }

    @Override
    public void setOriginId(String originId) {
        this.originId = originId;
    }

    @Override
    public String getOriginPlatform() {
        return originPlatform;
    }

    @Override
    public void setOriginPlatform(String originPlatform) {
        this.originPlatform = originPlatform;
    }

    @Override
    public String getOriginType() {
        return originType;
    }

    @Override
    public void setOriginType(String originType) {
        this.originType = originType;
    }

    @Override
    public String getDigest() {
        return digest;
    }

    @Override
    public void setDigest(String digest) {
        this.digest = digest;
    }

    @Override
    public String getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getOriginOwnerUsername() {
        return originOwnerUsername;
    }

    @Override
    public void setOriginOwnerUsername(String originOwnerUsername) {
        this.originOwnerUsername = originOwnerUsername;
    }
}
