package com.test.sensor.controller;

import com.test.sensor.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(SwaggerController.class)
@WithMockUser
public class SwaggerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    void testRedirectToSwagger() throws Exception {
        mockMvc.perform(get("/")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())  // Expecting HTTP status 3xx (redirection)
                .andExpect(redirectedUrl("/swagger-ui/index.html"));  // Expecting redirection to Swagger UI
    }
}
