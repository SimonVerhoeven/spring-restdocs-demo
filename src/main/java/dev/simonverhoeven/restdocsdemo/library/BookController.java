package dev.simonverhoeven.restdocsdemo.library;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    List<Book> books;

    public BookController() {
        books = List.of(
                new Book("978-0596809485", "97 things every programmer should know", "Kevlin Henney"),
                new Book("978-1491952696", "97 things every Java programmer should know", "Kevlin Henney & Trisha Gee"),
                new Book("978-1617294549", "Microservices Patterns: With examples in Java", "Chris Richardson")
        );
    }

    @GetMapping
    public List<Book> getBooks() {
        return books;
    }

    @GetMapping("{isbn}")
    public Book getBook(@PathVariable String isbn) {
        return books.stream().filter(book -> isbn.equals(book.isbn())).findFirst().orElse(null);
    }
}
