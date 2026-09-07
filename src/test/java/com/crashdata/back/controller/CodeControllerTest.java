package com.crashdata.back.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodeController.class)
class CodeControllerTest {

    @Autowired
    MockMvc mockMvc;

    // Listing all 30 lists here would just restate the map, so this covers the count plus
    // three enums whose codes are not a plain 1..n run
    @Test
    void getCodesServesEveryEnum() throws Exception {
        mockMvc.perform(get("/codes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(30)))
                .andExpect(content().json("""
                        {
                          "weather": [1, 2, 3, 4, 5, 6, 8, 9],
                          "crashSeverity": [1, 2, 3],
                          "manoeuvre": [1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 8, 9]
                        }
                        """, JsonCompareMode.LENIENT));
    }
}
