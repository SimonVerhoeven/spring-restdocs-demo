package dev.simonverhoeven.restdocsdemo.library;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn) {
        super("The book with isbn '" + isbn + "' wasn't found");
    }
}
