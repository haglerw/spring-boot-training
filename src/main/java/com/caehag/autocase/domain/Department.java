package com.caehag.autocase.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Department domain
 *
 * This will hold the departments for a given establishment
 *
 * @author Hagler Wafula
 * @version 1.0
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "department")
public class Department implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String departmentId;
    @Column(unique = true)
    private String departmentName;
    @Column(updatable = false)
    @CreationTimestamp
    private Date createdOn;
    @Column(insertable = false)
    @UpdateTimestamp
    private Date modifiedOn;
}
