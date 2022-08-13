package com.caehag.autocase.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Privilege domain
 *
 * These are the privileges available for the application
 *
 * @author Hagler Wafula
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "privilege")
public class Privilege implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String privilegeId;
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private List<Role> roles;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdOn;

    @UpdateTimestamp
    @Column(insertable = false)
    private Date modifiedOn;
}
