package org.ICIQ.eChempad.entities;

import org.ICIQ.eChempad.configurations.Converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Used to store information about the Authority table, which contains all authorities.
 */
@Entity
@Table(name="authority", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uuid"})
})
public class Authority {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Convert(converter = UUIDConverter.class)
    @Column(name = "uuid")
    private UUID uuid;

    @OneToMany
    private Set<Researcher> researchers = new HashSet<>();

    private String authorityName;
}
