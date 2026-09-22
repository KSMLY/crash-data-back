package com.crashdata.back.config;

import com.crashdata.back.controller.CodeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodeController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void requestWithoutATokenIsRejected() throws Exception{
        mockMvc.perform(get("/codes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithATokenIsApproved() throws Exception{
        mockMvc.perform(get("/codes").with(jwt()))
                .andExpect(status().isOk());
    }

}
