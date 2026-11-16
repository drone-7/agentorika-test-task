package org.drone.controller;

import org.drone.service.PhoneAvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhoneAvailabilityController.class)
class PhoneAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhoneAvailabilityService service;

    @Test
    void checkAvailability_available_returns200WithTrue() throws Exception {
        when(service.isAvailable("p1", "79991234567")).thenReturn(true);

        mockMvc.perform(get("/providers/p1/subscribers/79991234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void checkAvailability_notAvailable_returns200WithFalse() throws Exception {
        when(service.isAvailable("p1", "79991234567")).thenReturn(false);

        mockMvc.perform(get("/providers/p1/subscribers/79991234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void checkAvailability_providerNotFound_returns404() throws Exception {
        when(service.isAvailable("unknown", "79991234567"))
                .thenThrow(new IllegalArgumentException("Provider not found: unknown"));

        mockMvc.perform(get("/providers/unknown/subscribers/79991234567"))
                .andExpect(status().isNotFound());
    }
}
