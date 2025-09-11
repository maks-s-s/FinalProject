package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.Book;

public class BookItemDTO {
    private String bookName;
    private Integer quantity;

    public BookItemDTO(String bookName, Integer quantity) {
        this.bookName = bookName;
        this.quantity = quantity;
    }

    public BookItemDTO() {
    }
}
