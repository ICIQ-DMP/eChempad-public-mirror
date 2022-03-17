package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ICIQ.eChempad.configurations.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Permission;
import java.util.*;


/**
 * Used to store information about the User and its workspace.
 *
 * It has a list containing the different Journal that conform the workspace.
 */
@Entity
@Table(name="researcher", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID", "email"})
})
public class Researcher implements Serializable, IEntity {
    /*
     * https://stackoverflow.com/questions/45086957/how-to-generate-an-auto-uuid-using-hibernate-on-spring-boot/45087148
     * https://thorben-janssen.com/generate-uuids-primary-keys-hibernate/
     * https://stackoverflow.com/questions/43056220/store-uuid-v4-in-mysql (psql stores in binary but displays properly)
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Convert(converter = UUIDConverter.class)
    @Column(name = "UUID")
    private UUID uuid;

    @Column(name = "name", length = 1000, nullable = false)
    private String name;

    @Column(name = "email", length = 1000, nullable = false)
    private String email;

    //RF set a certain length for the used hashed algorithm
    @Column(name = "hashedPassword", length = 1000, nullable = false)
    private String hashedPassword;

    // Exactly 73 characters
    @Column(name = "signalsAPIKey", length = 73, nullable = true)
    private String signalsAPIKey;

    @OneToMany(
            targetEntity = ElementPermission.class,
            mappedBy = "researcher",
            fetch = FetchType.EAGER,
            // if a researcher is deleted all of its Permissions have to be deleted.
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    @MapKey(name = "id")
    @JsonIgnore
    private List<ElementPermission> permissions;


    // TODO: We use only one role per user to simplify, but it might get converted to a list of roles + table for roles
    //       and researchers.
    @Column(name = "role", length = 100, nullable = false)
    private Role role;

    /**
     * Used to create "ghost" instances only with the internal UUIDs in order to perform deletion.
     * This is used by springboot since it uses reflection to call the methods.
     * @param uuid
     */
    public Researcher(UUID uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Internally used by SpringBoot when using reflection.
     */
    public Researcher() {}

    /**
     * Constructor
     * @param fullName First name
     * @param email valid e-mail direction.
     */
    public Researcher(String fullName, String email, String signalsAPIKey, String hashedPassword) {
        this.name = fullName;
        this.email = email;
        this.signalsAPIKey = signalsAPIKey;
        this.permissions = new LinkedList<>();
        this.hashedPassword = hashedPassword;
        this.role = Role.USER;
    }

    public Researcher(String fullName, String email, String signalsAPIKey, String hashedPassword, Role role) {
        this.name = fullName;
        this.email = email;
        this.signalsAPIKey = signalsAPIKey;
        this.permissions = new LinkedList<>();
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Researcher{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", signalsAPIKey='" + signalsAPIKey + '\'' +
                //", permissions=" + permissions +
                ", role=" + role +
                '}';
    }


    // GETTERS AND SETTERS


    public UUID getUUid() {
        return this.uuid;
    }

    public void setUUid(UUID s) {
        this.uuid = s;
    }

    @Override
    public Class<?> getMyType() {
        return Researcher.class;
    }

    public String getFullName() {
        return this.name;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignalsAPIKey() {
        return this.signalsAPIKey;
    }

    public void setSignalsAPIKey(String signalsAPIKey) {
        this.signalsAPIKey = signalsAPIKey;
    }

    public List<ElementPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<ElementPermission> permissions) {
        this.permissions = permissions;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
