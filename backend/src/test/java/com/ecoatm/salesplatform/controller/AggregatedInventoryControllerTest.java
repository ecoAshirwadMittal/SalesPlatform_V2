package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AggregatedInventoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AggregatedInventoryControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private AggregatedInventoryService service;
    @MockBean private WeekRepository weekRepo;

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /weeks returns weeks ordered by start desc")
    void weeks_returnsOrderedList() throws Exception {
        Week w = new Week();
        java.lang.reflect.Field idField = Week.class.getDeclaredField("id");
        idField.setAccessible(true); idField.set(w, 100L);
        java.lang.reflect.Field dispField = Week.class.getDeclaredField("weekDisplay");
        dispField.setAccessible(true); dispField.set(w, "2026 / Wk17");
        java.lang.reflect.Field startField = Week.class.getDeclaredField("weekStartDateTime");
        startField.setAccessible(true); startField.set(w, Instant.parse("2026-04-20T00:00:00Z"));
        java.lang.reflect.Field endField = Week.class.getDeclaredField("weekEndDateTime");
        endField.setAccessible(true); endField.set(w, Instant.parse("2026-04-27T00:00:00Z"));

        when(weekRepo.findAllByOrderByWeekStartDateTimeDesc()).thenReturn(List.of(w));

        mvc.perform(get("/api/v1/admin/inventory/weeks"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].id").value(100))
           .andExpect(jsonPath("$[0].weekDisplay").value("2026 / Wk17"));
    }
}
