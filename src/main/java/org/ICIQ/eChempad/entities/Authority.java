package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ICIQ.eChempad.configurations.Converters.UUIDConverter;
import org.ICIQ.eChempad.configurations.Helpers.AuthorityUsernameId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.UUID;

@Entity
@IdClass(AuthorityUsernameId.class)
@Table(
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"id"})
        }
    )
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Convert(converter = UUIDConverter.class)
    @Column
    private UUID id;

    @Id
    @ManyToOne(targetEntity = Researcher.class, optional = false)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private String username;

    @Id
    @Column(length = 50, nullable = false)
    private String authority;

    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public Authority() {}

    public Authority(UUID id, String username, String authority) {
        this.id = id;
        this.username = username;
        this.authority = authority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
