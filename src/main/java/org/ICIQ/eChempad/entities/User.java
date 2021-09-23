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
@Table(name="User", uniqueConstraints = {
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
    private final UUID id;

    @Column(name = "fullName", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    // Exactly 73 characters
    @Column(name = "signalsAPIKey", length = 73)  // nullable = true
    private String signalsAPIKey;

    @OneToMany(targetEntity=Journal.class, mappedBy="id", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    private Set<Journal> journals;


    /**
     * Constructor
     * @param fullName First name
     * @param email valid e-mail direction.
     */
    public User(String fullName, String email) {
        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.email = email;
        this.journals = new HashSet<>();
    }


    // GETTERS AND SETTERS


    public UUID getUUid() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Set<Journal> getJournal() {
        return this.journals;
    }

    public void setJournals(Set<Journal> accessibleElements) {
        this.journals = accessibleElements;
    }
}
