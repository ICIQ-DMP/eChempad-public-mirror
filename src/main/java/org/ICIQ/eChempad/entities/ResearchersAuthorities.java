package org.ICIQ.eChempad.entities;

import org.ICIQ.eChempad.configurations.Converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "researchers_authorities")
public class ResearchersAuthorities {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Convert(converter = UUIDConverter.class)
    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne
    private Authority authority;

    @ManyToOne
    private Researcher researcher;
}
