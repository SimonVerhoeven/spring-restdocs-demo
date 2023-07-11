package dev.simonverhoeven.restdocsdemo.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Extensions to generate the documentation
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(BookController.class)
public class BookControllerTest {
    // Field descriptors that we'll be reusing
    private final FieldDescriptor[] bookDescriptor = new FieldDescriptor[]{
            fieldWithPath("isbn").description("ISBN-13 of the book"),
            fieldWithPath("title").description("Title of the book"),
            fieldWithPath("author").description("Author(s) of the book")
    };

    private final FieldDescriptor[] problemDetailsDescriptor = new FieldDescriptor[]{
            fieldWithPath("type").description("An URI reference [RFC3986] that identifies the problem type"),
            fieldWithPath("title").description("A short, human-readable summary of the problem type."),
            fieldWithPath("status").description("The HTTP status code ([RFC7231], Section 6) generated by the origin server for this occurrence of the problem."),
            fieldWithPath("detail").description("A human-readable explanation specific to this occurrence of the problem."),
            fieldWithPath("instance").description("A URI reference that identifies the specific occurrence of the problem.")
    };

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) { //we also need the RestDocumentationContextProvider
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(
                        // Note the document configuration
                        documentationConfiguration(restDocumentation)
                                // We can define the default encoding such as UTF-16
                                .snippets().withEncoding("UTF-8").and()
                                // We can modify the headers to add/remove some by default
                                .operationPreprocessors().withRequestDefaults(modifyHeaders().remove("Foo"))
                                // We can also define some reponse preprocessors such as pretty printing the content
                                .withResponseDefaults(prettyPrint()).and()
                                // Configure the default configured URI so that we don't see the default localhost, if the port is the default for the schema it gets omitted from the URL
                                .uris().withScheme("https").withHost("simonverhoeven.dev").withPort(443)
                )
                .build();
    }

    @Test
    void getBooks() throws Exception {
        this.mockMvc.perform(get("/book"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        // Here we're documenting it with the identifier getBooks, and defining the fields
                        document(
                                "getBooks",
                                responseFields(fieldWithPath("[]")
                                        .description("The book collection"))
                                        // this is done since we'll get an array which will contain our books
                                        .andWithPrefix("[].", bookDescriptor)
                        )
                );
    }

    @Test
    void getBook() throws Exception {
        this.mockMvc.perform(get("/book/{isbn}", "978-1491952696"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "getBook",
                                responseFields(bookDescriptor),
                                responseCookies(cookieWithName("checkedBook").description("The checked book ISBN as a cookie")),
                                pathParameters(
                                        parameterWithName("isbn").description("The ISBN-13 of the book you want to retrieve")
                                )
                        )
                );
    }

    @Test
    void getBook_notFound() throws Exception {
        this.mockMvc.perform(get("/book/{isbn}", "978-1491952695").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andDo(
                        document(
                                "getBook_notFound",
                                responseFields(problemDetailsDescriptor),
                                pathParameters(
                                        parameterWithName("isbn").description("The ISBN-13 of the book you want to retrieve")
                                )
                        )
                );
    }

    @Test
    void addBook() throws Exception {
        // Here we're reading the constraints we've defined on the BookCreationDTO so that we can include them in our documentation
        ConstraintDescriptions bookCreationConstraints = new ConstraintDescriptions(BookCreationDTO.class);

        this.mockMvc.perform(post("/book").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(new BookCreationDTO("978-1633437975", "Spring Security in Action, Second Edition", "Laurentiu Spilca"))))
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

    @Test
    void addCover() throws Exception {
        final var coverImage = new MockMultipartFile("cover", "cover.png", "image/png", "<<cover data>>".getBytes());
        final var metadata = new MockMultipartFile("metadata", "", "application/json", "{ \"version\": \"1.0\"}".getBytes());

        this.mockMvc.perform(
                        multipart("/book/{isbn}/addCover", "978-1491952695").file(coverImage).file(metadata).contentType(MediaType.APPLICATION_JSON)
                                .header("secretHeader", "What is the meaning of life?")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "addCover",
                                // documenting headers
                                requestHeaders(headerWithName("secretHeader").description("A secret header")),
                                // documenting request part fields
                                requestPartFields("metadata", fieldWithPath("version").description("The version of the image")),
                                pathParameters(
                                        parameterWithName("isbn").description("The ISBN-13 of the book you want to upload the cover for")
                                ),
                                // documenting response headers
                                responseHeaders(headerWithName("secretResponseHeader").description("A secret response header"))
                        )
                );
    }
}
