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
    // three enums whose names are worth pinning down
    @Test
    void getCodesServesEveryEnum() throws Exception {
        mockMvc.perform(get("/codes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(30)))
                .andExpect(content().json("""
                        {
                          "weather": ["CLEAR", "RAIN", "SNOW", "FOG", "SLEET", "SEVERE_WINDS", "OTHER", "UNKNOWN"],
                          "crashSeverity": ["FATAL", "SERIOUS", "SLIGHT"],
                          "manoeuvre": ["REVERSING", "PARKED", "ENTERING_OR_LEAVING_PARKING", "SLOWING_OR_STOPPING",
                                        "MOVING_OFF", "WAITING_TO_TURN", "TURNING", "CHANGING_LANE",
                                        "AVOIDANCE_MANOEUVRE", "OVERTAKING", "STRAIGHT_FORWARD", "OTHER", "UNKNOWN"]
                        }
                        """, JsonCompareMode.LENIENT));
    }
}
