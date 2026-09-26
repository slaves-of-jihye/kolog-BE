package com.kogo.kologbackend.domains.user.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserExceptionIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    @Transactional
    void duplicateSignupReturnsConflict() throws Exception {
        String signup = """
                {"email":"conflict@example.org","password":"password","nickname":"tester"}
                """;
        mvc.perform(post("/api/v1/users/signup").contentType(APPLICATION_JSON).content(signup))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/users/signup").contentType(APPLICATION_JSON).content(signup))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void incorrectPasswordReturnsUnauthorized() throws Exception {
        mvc.perform(post("/api/v1/users/login").contentType(APPLICATION_JSON)
                        .content("""
                                {"email":"missing@example.org","password":"incorrect"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
