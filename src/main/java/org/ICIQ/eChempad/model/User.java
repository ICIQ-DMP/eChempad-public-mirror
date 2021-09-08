package org.ICIQ.eChempad.model;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
/**
 * Used to store information about the User and its workspace.
 *
 * It has a list containing the different Journal that conform the workspace.
 */
public class User {
    private UUID UUid;
    private String firstName;
    private String lastName;
    private String email;
    private String signalsAPIKey;
    private List<Journal> accessibleElements;


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
        this.accessibleElements = new ArrayList<>();
    }


    // GETTERS I SETTERS


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

    public List<Journal> getAccessibleElements() {
        return accessibleElements;
    }

    public void setAccessibleElements(List<Journal> accessibleElements) {
        this.accessibleElements = accessibleElements;
    }
}
