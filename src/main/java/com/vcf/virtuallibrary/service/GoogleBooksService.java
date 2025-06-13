package com.vcf.virtuallibrary.service;

import com.vcf.virtuallibrary.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksService {

    private final RestTemplate restTemplate;

    public GoogleBooksService() {
        this.restTemplate = new RestTemplate();
    }

    // Main method that handles both single and multiple results
    public List<Book> getBookDetails(String query, int maxResults) {
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/books/v1/volumes")
                .queryParam("q", query)
                .queryParam("maxResults", maxResults)
                .build().toUriString();

        JsonNode root = restTemplate.getForObject(url, JsonNode.class);

        List<Book> books = new ArrayList<>();

        if (root == null || !root.has("items") || !root.get("items").isArray()) {
            System.out.println("No books found");
            return books; // Return empty list
        }

        for (JsonNode volume : root.get("items")) {
            Book book = parseBookFromJson(volume);
            books.add(book);
        }

        System.out.println("Fetched " + books.size() + " books");
        return books;
    }

    // Convenience method for single book (backward compatibility)
    public Book getBookDetails(String query) {
        List<Book> books = getBookDetails(query, 1);
        return books.isEmpty() ? new Book() : books.get(0);
    }

    // Convenience method for multiple books with default of 5
    public List<Book> getMultipleBookDetails(String query) {
        return getBookDetails(query, 5);
    }

    // Private helper method to parse a single book from JSON
    private Book parseBookFromJson(JsonNode volume) {
        JsonNode volumeInfo = volume.path("volumeInfo");
        Book book = new Book();

        book.setTitle(volumeInfo.path("title").asText("Unknown Title"));

        if (volumeInfo.has("authors") && volumeInfo.get("authors").isArray()) {
            List<String> authorsList = new ArrayList<>();
            for (JsonNode author : volumeInfo.get("authors")) {
                authorsList.add(author.asText());
            }
            book.setAuthors(String.join(", ", authorsList));
        } else {
            book.setAuthors("Unknown Author");
        }

        if (volumeInfo.has("categories") && volumeInfo.get("categories").isArray()) {
            List<String> categoriesList = new ArrayList<>();
            for (JsonNode category : volumeInfo.get("categories")) {
                categoriesList.add(category.asText());
            }
            book.setCategory(String.join(", ", categoriesList));
        } else {
            book.setCategory("Uncategorized");
        }

        JsonNode imageLinks = volumeInfo.path("imageLinks");
        String thumbnail = imageLinks.path("thumbnail").asText(null);
        book.setThumbnailUrl(thumbnail);

        return book;
    }
}