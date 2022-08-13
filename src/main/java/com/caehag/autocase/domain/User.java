package com.caehag.autocase.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * User domain
 *
 * Holds the user entity class
 *
 * @author Hagler Wafula
 * @version 1.0
 */
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    private Boolean isActive;
    private Boolean isNotLocked;
    @Column(updatable = false)
    @CreationTimestamp
    private Date createdOn;
    @Column(insertable = false)
    @UpdateTimestamp
    private Date modifiedOn;
}
