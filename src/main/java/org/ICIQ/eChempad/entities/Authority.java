package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.ICIQ.eChempad.configurations.Converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Authority implements Serializable, IEntity, GrantedAuthority{
    @Id
    @Convert(converter = UUIDConverter.class)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, unique = true)
    protected UUID id;

    // Authority that this user has against this resource.
    @Column(name = "authority", length = 100, nullable = false)
    protected String authority;


    @JoinColumn(name = "researcher")
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false,
            cascade = CascadeType.ALL
    )
    @JsonBackReference
    protected Researcher researcher;

    public Authority() {}

    public Authority(String authority, Researcher researcher) {
        this.authority = authority;
        this.researcher = researcher;
    }

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public Serializable getId() {
        return this.id;
    }

    @Override
    public void setId(Serializable id) {
        this.id = (UUID)id;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "authority='" + authority + '\'' +
                ", researcher=" + researcher +
                '}';
    }
}
