package dev.simonverhoeven.restdocsdemo.library;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.ISBN;

public record BookCreationDTO(
        @NotNull
        @ISBN
        String isbn,
        @NotNull
        @Size(max=120)
        String title,
        @NotNull
        @Size(max=60)
        String author
) {}
