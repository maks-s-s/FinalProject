package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BOOK_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
