package org.ICIQ.eChempad.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;


/**
 * Used to store information about the User and its workspace.
 *
 * It has a list containing the different Journal that conform the workspace.
 */
@Entity
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class User {
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
    @Column(name = "id", nullable = false, updatable = false)
    private final UUID UUid;

    @Column(name = "firstName", length = 100, nullable = false)
    private String firstName;

    @Column(name = "lastName", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    /*
     * Exactly 73 characters
     */
    @Column(name = "signalsAPIKey", length = 73)
    private String signalsAPIKey;

    @OneToMany(targetEntity=Journal.class, mappedBy="UUid", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    private Set<Journal> accessibleElements;


    /**
     * Constructor
     * @param firstName First name
     * @param lastName Last name
     * @param email valid e-mail direction.
     */
    public User(String firstName, String lastName, String email) {
        this.UUid = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accessibleElements = new HashSet<>();
    }


    // GETTERS AND SETTERS


    public UUID getUUid() {
        return UUid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignalsAPIKey() {
        return signalsAPIKey;
    }

    public void setSignalsAPIKey(String signalsAPIKey) {
        this.signalsAPIKey = signalsAPIKey;
    }

    public Set<Journal> getAccessibleElements() {
        return accessibleElements;
    }

    public void setAccessibleElements(Set<Journal> accessibleElements) {
        this.accessibleElements = accessibleElements;
    }
}
