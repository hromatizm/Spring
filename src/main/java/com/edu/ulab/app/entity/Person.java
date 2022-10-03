package com.edu.ulab.app.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "person", schema = "ulab_edu")
public class Person {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequence")
    @SequenceGenerator(
            name = "sequence",
            sequenceName = "sequence",
            allocationSize = 100)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String country;

//    @OneToMany(mappedBy = "person", cascade = {
//            CascadeType.MERGE,
//            CascadeType.PERSIST,
//            CascadeType.DETACH,
//            CascadeType.REFRESH})
//    private Set<Book> bookSet;
}
