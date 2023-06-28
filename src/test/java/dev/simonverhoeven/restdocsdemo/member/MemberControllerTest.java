package dev.simonverhoeven.restdocsdemo.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Extensions to generate the documentation
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) { //we also need the RestDocumentationContextProvider
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)) // Note the document configuration
                .build();
    }

    @Test
    void getMember() throws Exception {
        this.mockMvc.perform(get("/member/{memberId}", "0819B178E55090F09730BDAA2B9AC1F329CD1244"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "getMember",
                                responseFields(
                                        // documenting a subsection, so we don't have to document everything
                                        subsectionWithPath("contact").description("The member's contact information"),
                                        // let's ignore this field
                                        fieldWithPath("dueBalance").ignored(),
                                        fieldWithPath("memberId").description("The member's identifier"),
                                        fieldWithPath("fullName").description("The full name of the member")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").description("The member's identifier")
                                )
                        )
                );
    }

    @Test
    void getMember_subsectionFields() throws Exception {
        this.mockMvc.perform(get("/member/{memberId}", "0819B178E55090F09730BDAA2B9AC1F329CD1244"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "getMember_subsectionFields",
                                // Documenting the concact fields
                                responseFields(
                                        beneathPath("contact"),
                                        fieldWithPath("eMail").description("The member's e-mail"),
                                        fieldWithPath("mobileNumber").description("The member's mobile number")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").description("The member's identifier")
                                )
                        )
                );
    }

    @Test
    void getMember_subsection() throws Exception {
        this.mockMvc.perform(get("/member/{memberId}", "0819B178E55090F09730BDAA2B9AC1F329CD1244"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "getMember_subsection",
                                //documenting the contact part of the member response
                                responseBody(beneathPath("contact")),
                                pathParameters(
                                        parameterWithName("memberId").description("The member's identifier")
                                )
                        )
                );
    }

}
