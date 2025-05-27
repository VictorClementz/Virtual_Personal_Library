package com.vcf.virtuallibrary.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.vcf.virtuallibrary.model.Book;
import com.vcf.virtuallibrary.repository.BookRepository;
import com.vcf.virtuallibrary.service.GoogleBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {



    private final BookRepository bookRepository;

    @Autowired
    private GoogleBooksService googleBooksService;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // CREATE
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    // READ ALL
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    ///fix later
                /**  book.setTitle(updatedBook.getTitle());
                    book.setAuthors(updatedBook.getAuthors());
                    book.setCategory(updatedBook.getCategory());
                    book.setThumbnailUrl(updatedBook.getThumbnailUrl());
                   */ return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "Book deleted successfully!";
    }

    @GetMapping("/fetch-book")
    public Book getBookDetails(@RequestParam String query) {
        return googleBooksService.getBookDetails(query);
    }
}
