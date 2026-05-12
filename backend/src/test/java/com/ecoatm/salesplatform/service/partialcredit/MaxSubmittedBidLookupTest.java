package com.ecoatm.salesplatform.service.partialcredit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MaxSubmittedBidLookup}. The SQL is verified
 * via ArgumentCaptor — we don't want a typo'd column name to slip
 * through, and a real Postgres roundtrip belongs in an IT (Chunk 3
 * already includes one for the credit-calc end-to-end path).
 */
@ExtendWith(MockitoExtension.class)
class MaxSubmittedBidLookupTest {

    @Mock JdbcTemplate jdbc;

    MaxSubmittedBidLookup lookup;

    @BeforeEach
    void setUp() {
        lookup = new MaxSubmittedBidLookup(jdbc);
    }

    @Test
    @DisplayName("maxSubmittedBid returns aggregate value when JDBC returns a number")
    void returnsValue_whenJdbcReturnsAmount() {
        when(jdbc.queryForObject(anyString(), eq(BigDecimal.class),
                eq(123L), eq("ECO-APPLE-XR-64"), eq("A_R")))
                .thenReturn(new BigDecimal("85.5000"));

        Optional<BigDecimal> result = lookup.maxSubmittedBid(123L,
                "ECO-APPLE-XR-64", "A_R");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualByComparingTo("85.5000");
    }

    @Test
    @DisplayName("maxSubmittedBid passes weekId, ecoid, mergedGrade as positional params")
    void passesParametersInOrder() {
        when(jdbc.queryForObject(anyString(), eq(BigDecimal.class),
                eq(7L), eq("ECO-X"), eq("B_R")))
                .thenReturn(new BigDecimal("10"));

        lookup.maxSubmittedBid(7L, "ECO-X", "B_R");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> argsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(jdbc).queryForObject(sqlCaptor.capture(), eq(BigDecimal.class),
                argsCaptor.capture(), argsCaptor.capture(), argsCaptor.capture());

        // Sanity-check the SQL hits the right columns + tables — guards
        // against a rename of bid_data.ecoid or merged_grade going
        // unnoticed.
        String sql = sqlCaptor.getValue();
        assertThat(sql).contains("auctions.bid_data");
        assertThat(sql).contains("auctions.bid_rounds");
        assertThat(sql).contains("auctions.scheduling_auctions");
        assertThat(sql).contains("auctions.auctions");
        assertThat(sql).contains("MAX(bd.submitted_bid_amount)");
        assertThat(sql).contains("a.week_id");
        assertThat(sql).contains("bd.ecoid");
        assertThat(sql).contains("bd.merged_grade");
        assertThat(sql).contains("bd.submitted_bid_amount IS NOT NULL");

        assertThat(argsCaptor.getAllValues()).containsExactly(7L, "ECO-X", "B_R");
    }

    @Test
    @DisplayName("maxSubmittedBid returns empty when JdbcTemplate yields null")
    void returnsEmpty_whenJdbcReturnsNull() {
        when(jdbc.queryForObject(anyString(), eq(BigDecimal.class),
                eq(99L), eq("ECO-Y"), eq("C_R")))
                .thenReturn(null);

        Optional<BigDecimal> result = lookup.maxSubmittedBid(99L, "ECO-Y", "C_R");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("maxSubmittedBid returns empty when JdbcTemplate throws EmptyResultDataAccessException")
    void returnsEmpty_whenEmptyResultException() {
        when(jdbc.queryForObject(anyString(), eq(BigDecimal.class),
                eq(1L), eq("ECO-NULL"), eq("Z_R")))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<BigDecimal> result = lookup.maxSubmittedBid(1L, "ECO-NULL", "Z_R");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("maxSubmittedBid short-circuits to empty when weekId is null")
    void returnsEmpty_whenWeekIdNull() {
        Optional<BigDecimal> result = lookup.maxSubmittedBid(null, "ECO-X", "A_R");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("maxSubmittedBid short-circuits to empty when ecoatmCode is blank")
    void returnsEmpty_whenEcoatmCodeBlank() {
        Optional<BigDecimal> result = lookup.maxSubmittedBid(1L, "  ", "A_R");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("maxSubmittedBid short-circuits to empty when mergedGrade is null")
    void returnsEmpty_whenGradeNull() {
        Optional<BigDecimal> result = lookup.maxSubmittedBid(1L, "ECO-X", null);

        assertThat(result).isEmpty();
    }
}
