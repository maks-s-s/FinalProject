package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .toList();
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book saved = bookRepository.findByName(name)
                .orElseThrow(NotFoundException::new);
        return modelMapper.map(saved, BookDTO.class);
    }

    @Override
    @Transactional
    public BookDTO updateBookByName(String name, BookDTO book) {
        Book bookFromDB = bookRepository.findByName(name)
                .orElseThrow(NotFoundException::new);

        modelMapper.map(book, bookFromDB);

        Book updated = bookRepository.save(bookFromDB);
        return modelMapper.map(updated, BookDTO.class);
    }

    @Override
    @Transactional
    public void deleteBookByName(String name) {
        bookRepository.removeByName(name);
    }

    @Override
    @Transactional
    public BookDTO addBook(BookDTO book) {
        try {
            Book saved = bookRepository.save(modelMapper.map(book, Book.class));
            return modelMapper.map(saved, BookDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException();
        }
    }
}
