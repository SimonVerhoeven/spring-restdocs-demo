package dev.simonverhoeven.restdocsdemo.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(BookController.class)
public class BookControllerTest {
    // Field descriptors that we'll be reusing
    private final FieldDescriptor[] bookDescriptor = new FieldDescriptor[] {
            fieldWithPath("isbn").description("ISBN-13 of the book"),
            fieldWithPath("title").description("Title of the book"),
            fieldWithPath("author").description("Author(s) of the book")
    };

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void getBooks() throws Exception {
        this.mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "getBooks",
                                responseFields(fieldWithPath("[]")
                                        .description("The book collection"))
                                        .andWithPrefix("[].", bookDescriptor)
                        )
                );
    }

    @Test
    public void getBook() throws Exception {
        this.mockMvc.perform(get("/books/{isbn}", "978-1491952696"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "getBook",
                                responseFields(bookDescriptor),
                                pathParameters(
                                        parameterWithName("isbn").description("The ISBN-13 of the book you want to retrieve")
                                )
                        )
                );
    }

    @Test
    public void addBook() throws Exception {
        ConstraintDescriptions bookCreationConstraints = new ConstraintDescriptions(BookCreationDTO.class);

        this.mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(new BookCreationDTO("978-1633437975", "Spring Security in Action, Second Edition", "Laurentiu Spilca"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "addBook",
                                requestFields(
                                        fieldWithPath("isbn").description("ISBN-13 of the book" + bookCreationConstraints.descriptionsForProperty("isbn")),
                                        fieldWithPath("title").description("Title of the book" + bookCreationConstraints.descriptionsForProperty("title")),
                                        fieldWithPath("author").description("Author(s) of the book" + bookCreationConstraints.descriptionsForProperty("author"))
                                ),
                                responseFields(bookDescriptor)
                        )
                );
    }
}
