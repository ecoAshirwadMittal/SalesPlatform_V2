package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.ReserveBidListResponse;
import com.ecoatm.salesplatform.dto.ReserveBidRow;
import com.ecoatm.salesplatform.dto.ReserveBidUploadResult;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReserveBidController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class ReserveBidControllerIT {

    @Autowired
    MockMvc mvc;

    @MockBean
    ReserveBidService service;

    @Test
    @WithMockUser(roles = "Administrator")
    void list_returnsPayload() throws Exception {
        when(service.search(isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(new ReserveBidListResponse(List.of(), 0L, 0, 10));

        mvc.perform(get("/api/v1/admin/reserve-bids?page=0&size=10"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.rows").exists())
           .andExpect(jsonPath("$.total").exists());
    }

    @Test
    @WithMockUser(roles = "Bidder")
    void nonAdmin_gets403() throws Exception {
        mvc.perform(get("/api/v1/admin/reserve-bids"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void create_andFetchById() throws Exception {
        ReserveBidRow row = new ReserveBidRow(1L, "88001", "A_YYY", "Test", "T1",
                new BigDecimal("5.00"), Instant.now(), null, null, null, Instant.now());
        when(service.create(anyLong(), any())).thenReturn(row);

        String body = """
                {"productId":"88001","grade":"A_YYY","brand":"Test","model":"T1",
                 "bid":5.00,"lastAwardedMinPrice":null,"lastAwardedWeek":null,"bidValidWeekDate":null}
                """;
        mvc.perform(post("/api/v1/admin/reserve-bids")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.productId").value("88001"));
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void upload_samplePasses() throws Exception {
        when(service.upload(anyLong(), any()))
                .thenReturn(new ReserveBidUploadResult(5, 0, 0, 5, List.of()));

        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/fixtures/reserve-bid-sample.xlsx"));
        MockMultipartFile file = new MockMultipartFile("file", "sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        mvc.perform(multipart("/api/v1/admin/reserve-bids/upload").file(file))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.errors").isArray());
    }
}
