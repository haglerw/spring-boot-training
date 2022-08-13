package com.caehag.autocase.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Principal Group domain
 *
 * Holds the roles for this application
 *
 * https://www.baeldung.com/role-and-privilege-for-spring-security-registration
 * https://www.baeldung.com/spring-security-granted-authority-vs-role
 *
 * @author Hagler Wafula
 * @version 1.0
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String roleId;
    @Column(unique = true)
    private String code;
    @Column(unique = true)
    private String name;
    @ManyToMany(cascade = { DETACH, MERGE, PERSIST, REFRESH })
    @JoinTable(
            name = "role_privilege",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private List<Privilege> privileges;
    @Column(updatable = false)
    @CreationTimestamp
    private Date createdOn;
    @Column(insertable = false)
    @UpdateTimestamp
    private Date modifiedOn;

    // convenience method to add privilege
    public void addPrivilege(Privilege tempPrivilege) {
        if (privileges == null) {
            privileges = new ArrayList<>();
        }

        privileges.add(tempPrivilege);
    }
}
