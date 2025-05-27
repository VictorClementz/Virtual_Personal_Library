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

    public Book getBookDetails(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/books/v1/volumes")
                .queryParam("q", query)
                .queryParam("maxResults", "1")
                .build().toUriString();

        JsonNode root = restTemplate.getForObject(url, JsonNode.class);

        if (root == null || !root.has("items") || !root.get("items").isArray() || root.get("items").size() == 0) {
            System.out.println("No books found for query: " + query);
            return new Book();  // Return an empty book (or null if you prefer)
        }

        JsonNode volume = root.get("items").get(0);  // Get the first result
        JsonNode volumeInfo = volume.path("volumeInfo");  // Use path() to avoid exceptions

        Book book = new Book();

        // Extract fields more safely
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

        // Debug printout
        System.out.println("Fetched Book: " + book);

        return book;
    }


}

