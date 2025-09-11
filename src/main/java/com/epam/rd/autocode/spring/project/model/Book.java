package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BOOKS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    private AgeGroup ageGroup;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "publication_year")
    private LocalDate publicationDate;

    private String author;

    @Column(name = "number_of_pages")
    private Integer pages;

    private String characteristics;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Language language;
}
