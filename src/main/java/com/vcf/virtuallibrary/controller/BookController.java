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

@Controller
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {


    private final BookRepository bookRepository;
    private final GoogleBooksService googleBooksService;

    public BookController(BookRepository bookRepository, GoogleBooksService googleBooksService) {
        this.bookRepository = bookRepository;
        this.googleBooksService = googleBooksService;
    }

   // json fronntend
    @GetMapping("/search-json")
    @ResponseBody
    public Book fetchBookDetailsJson(@RequestParam String query) {
        return googleBooksService.getBookDetails(query);
    }


    @PostMapping
    @ResponseBody
    public Book addBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping
    @ResponseBody
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    @PutMapping("/{id}")
    @ResponseBody
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
    @ResponseBody
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.ok("Book deleted successfully!");
    }

    // old thymeleaf not used rn
    @GetMapping("/fetch-book")
    public String fetchBookDetails(@RequestParam String query, Model model) {
        Book book = googleBooksService.getBookDetails(query);
        model.addAttribute("book", book);
        return "search";
    }

    @GetMapping("/search")
    public String showSearchForm() {
        return "search";
    }

}
