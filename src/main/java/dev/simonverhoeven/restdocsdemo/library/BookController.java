package dev.simonverhoeven.restdocsdemo.library;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
        return books.stream().filter(book -> isbn.equals(book.isbn())).findFirst().orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @PostMapping
    public Book addBook(@Valid @RequestBody BookCreationDTO bookCreation) {
        final var book = new Book(bookCreation.isbn(), bookCreation.title(), bookCreation.author());
        books.add(book);
        return book;
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleBookNotFound(BookNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Book not found");
        problemDetail.setType(URI.create("https://somelibrary.com/books/not-found"));
        return problemDetail;
    }

}
