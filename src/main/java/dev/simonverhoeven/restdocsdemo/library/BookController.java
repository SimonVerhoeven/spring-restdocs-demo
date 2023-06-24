package dev.simonverhoeven.restdocsdemo.library;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/books")
public class BookController {
    List<Book> books;

    public BookController() {
        books = Stream.of(
                new Book("978-0596809485", "97 things every programmer should know", "Kevlin Henney"),
                new Book("978-1491952696", "97 things every Java programmer should know", "Kevlin Henney & Trisha Gee"),
                new Book("978-1617294549", "Microservices Patterns: With examples in Java", "Chris Richardson")
        ).collect(Collectors.toList());
    }

    @GetMapping
    public List<Book> getBooks() {
        return books;
    }

    @GetMapping("{isbn}")
    public Book getBook(@PathVariable String isbn) {
        return books.stream().filter(book -> isbn.equals(book.isbn())).findFirst().orElse(null);
    }

    @PostMapping
    public Book addBook(@Valid @RequestBody BookCreationDTO bookCreation) {
        final var book = new Book(bookCreation.isbn(), bookCreation.title(), bookCreation.author());
        books.add(book);
        return book;
    }
}
