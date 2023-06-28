package dev.simonverhoeven.restdocsdemo.library;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/book")
public class BookController {
    private final List<Book> books;

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
    public Book getBook(
            @PathVariable String isbn,
            HttpServletResponse httpServletResponse) {
        final var cookie = new Cookie("checkedBook", isbn);
        httpServletResponse.addCookie(cookie);
        return books.stream().filter(book -> isbn.equals(book.isbn())).findFirst().orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @PostMapping
    public Book addBook(@Valid @RequestBody BookCreationDTO bookCreation) {
        final var book = new Book(bookCreation.isbn(), bookCreation.title(), bookCreation.author());
        books.add(book);
        return book;
    }

    @PostMapping("/{isbn}/addCover")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Void> handleFileUpload(
            @RequestHeader("secretHeader") String header,
            @PathVariable String isbn,
            @RequestParam("cover") MultipartFile files
    ) {
        // file handling logic
        return ResponseEntity.ok().header("secretResponseHeader", "42").build();
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleBookNotFound(BookNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Book not found");
        problemDetail.setType(URI.create("https://somelibrary.com/book/not-found"));
        return problemDetail;
    }

}
