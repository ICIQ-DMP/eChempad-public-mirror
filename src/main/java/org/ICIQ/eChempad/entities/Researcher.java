package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ICIQ.eChempad.configurations.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


/**
 * Used to store information about the User and its workspace.
 *
 * It has a list containing the different Journal that conform the workspace.
 */
@Entity
@Table(name="researcher", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
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

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    // Exactly 73 characters
    @Column(name = "signalsAPIKey", length = 73)  // nullable = true
    private String signalsAPIKey;

    @OneToMany(
            targetEntity = JournalPermission.class,
            mappedBy = "researcher",
            fetch = FetchType.EAGER
    //        cascade = CascadeType.ALL
    )
    @JsonIgnore
    private Set<JournalPermission> journals;

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
    public Researcher(String fullName, String email, String signalsAPIKey) {
        this.name = fullName;
        this.email = email;
        this.signalsAPIKey = signalsAPIKey;
        this.journals = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Researcher{" +
                "id=" + uuid +
                ", fullName='" + name + '\'' +
                ", email='" + email + '\'' +
                ", signalsAPIKey='" + signalsAPIKey + '\'' +
                ", journals=" + journals +
                '}';
    }


    // GETTERS AND SETTERS


    public UUID getUUid() {
        return this.uuid;
    }

    public void setUUid(UUID s) {
        this.uuid = s;
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

    public Set<JournalPermission> getJournals() {
        return this.journals;
    }

    public void setJournals(Set<JournalPermission> journals) {
        this.journals = journals;
    }

}
