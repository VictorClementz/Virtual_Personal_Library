package com.vcf.virtuallibrary.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.vcf.virtuallibrary.model.Book;
import com.vcf.virtuallibrary.repository.BookRepository;
import com.vcf.virtuallibrary.service.GoogleBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    private final BookRepository bookRepository;
    private final GoogleBooksService googleBooksService;

    public BookController(BookRepository bookRepository, GoogleBooksService googleBooksService) {
        this.bookRepository = bookRepository;
        this.googleBooksService = googleBooksService;
    }

    // json frontend - single result (backward compatibility)
    @GetMapping("/search-json")
    public Book fetchBookDetailsJson(@RequestParam String query) {
        return googleBooksService.getBookDetails(query);
    }

    // json frontend - flexible search with custom maxResults
    @GetMapping("/search")
    public List<Book> fetchBooksJson(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int maxResults) {
        return googleBooksService.getBookDetails(query, maxResults);
    }

    // Convenience endpoint for multiple results (uses default of 5)
    @GetMapping("/search-multiple")
    public List<Book> fetchMultipleBookDetailsJson(@RequestParam String query) {
        return googleBooksService.getMultipleBookDetails(query);
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthors(updatedBook.getAuthors());
                    book.setCategory(updatedBook.getCategory());
                    book.setThumbnailUrl(updatedBook.getThumbnailUrl());
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.ok("Book deleted successfully!");
    }

    // old thymeleaf not used rn
    @GetMapping("/fetch-book")
    public String fetchBookDetails(@RequestParam String query, Model model) {
        Book book = googleBooksService.getBookDetails(query);
        model.addAttribute("book", query);
        return "search";
    }

    @GetMapping("/search-form")
    public String showSearchForm() {
        return "search";
    }
}