package com.ecoatm.salesplatform.service.partialcredit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ResolveReceivedDeviceService}. We mock
 * {@link JdbcTemplate#query} and assert SQL shape + parameter wiring,
 * because the actual mdm.device + mdm.model rows live in a real DB —
 * that flow is exercised in a follow-on IT (Chunk 3).
 */
@ExtendWith(MockitoExtension.class)
class ResolveReceivedDeviceServiceTest {

    @Mock JdbcTemplate jdbc;

    ResolveReceivedDeviceService service;

    @BeforeEach
    void setUp() {
        service = new ResolveReceivedDeviceService(jdbc);
    }

    // -------------------------------------------------------------------
    // IMEI-shaped input
    // -------------------------------------------------------------------

    @Test
    @DisplayName("resolve returns empty for 15-digit IMEI input (Phase 1)")
    void imei_returnsEmpty() {
        // Phase 1: mdm.device has no IMEI column today. The lookup
        // short-circuits without hitting the DB.
        Optional<ResolveReceivedDeviceService.DeviceMatch> result =
                service.resolve("353918114149171");

        assertThat(result).isEmpty();
        verify(jdbc, never()).query(anyString(), any(RowMapper.class),
                (Object[]) any());
    }

    // -------------------------------------------------------------------
    // Model-name lookup — exact match
    // -------------------------------------------------------------------

    @Test
    @DisplayName("resolve returns device when exact model-name match succeeds")
    void modelName_exactMatch_returnsDevice() {
        when(jdbc.query(anyString(), any(RowMapper.class),
                eq("iPhone XR"), eq("iPhone XR")))
                .thenReturn(List.of(new ResolveReceivedDeviceService.DeviceMatch(
                        "ECO-APPLE-XR-64", "A_R", "Apple", "iPhone XR")));

        Optional<ResolveReceivedDeviceService.DeviceMatch> result =
                service.resolve("iPhone XR");

        assertThat(result).isPresent();
        assertThat(result.get().ecoatmCode()).isEqualTo("ECO-APPLE-XR-64");
        assertThat(result.get().mergedGrade()).isEqualTo("A_R");
        assertThat(result.get().brand()).isEqualTo("Apple");
        assertThat(result.get().model()).isEqualTo("iPhone XR");
    }

    @Test
    @DisplayName("resolve trims input before exact match")
    void modelName_trimsWhitespace() {
        when(jdbc.query(anyString(), any(RowMapper.class),
                eq("iPhone XR"), eq("iPhone XR")))
                .thenReturn(List.of(new ResolveReceivedDeviceService.DeviceMatch(
                        "ECO-APPLE-XR-64", "A_R", "Apple", "iPhone XR")));

        Optional<ResolveReceivedDeviceService.DeviceMatch> result =
                service.resolve("  iPhone XR  ");

        assertThat(result).isPresent();
        assertThat(result.get().ecoatmCode()).isEqualTo("ECO-APPLE-XR-64");
    }

    // -------------------------------------------------------------------
    // Model-name lookup — contains fallback
    // -------------------------------------------------------------------

    @Test
    @DisplayName("resolve falls back to contains-match when exact-match misses")
    void modelName_containsFallback() {
        // Exact match misses (first call), ILIKE %...% wins (second call).
        when(jdbc.query(anyString(), any(RowMapper.class),
                eq("XR"), eq("XR")))
                .thenReturn(List.of());
        when(jdbc.query(anyString(), any(RowMapper.class),
                eq("%XR%"), eq("%XR%")))
                .thenReturn(List.of(new ResolveReceivedDeviceService.DeviceMatch(
                        "ECO-APPLE-XR-128", "B_R", "Apple", "iPhone XR")));

        Optional<ResolveReceivedDeviceService.DeviceMatch> result =
                service.resolve("XR");

        assertThat(result).isPresent();
        assertThat(result.get().ecoatmCode()).isEqualTo("ECO-APPLE-XR-128");

        // Both SQL paths were executed — exact first, then contains.
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbc, times(2)).query(sqlCaptor.capture(), any(RowMapper.class),
                any(Object.class), any(Object.class));
        assertThat(sqlCaptor.getAllValues().get(0))
                .as("first call uses LOWER(...) = LOWER(?) exact-match shape")
                .contains("LOWER(m.name) = LOWER(?)");
        assertThat(sqlCaptor.getAllValues().get(1))
                .as("second call uses ILIKE for fuzzy fallback")
                .contains("m.name ILIKE ?");
    }

    // -------------------------------------------------------------------
    // Nothing matches
    // -------------------------------------------------------------------

    @Test
    @DisplayName("resolve returns empty when neither exact nor contains match")
    void modelName_noMatch_returnsEmpty() {
        when(jdbc.query(anyString(), any(RowMapper.class),
                eq("Nokia 3310"), eq("Nokia 3310")))
                .thenReturn(List.of());
        when(jdbc.query(anyString(), any(RowMapper.class),
                eq("%Nokia 3310%"), eq("%Nokia 3310%")))
                .thenReturn(List.of());

        Optional<ResolveReceivedDeviceService.DeviceMatch> result =
                service.resolve("Nokia 3310");

        assertThat(result).isEmpty();
        verify(jdbc, atLeastOnce()).query(anyString(), any(RowMapper.class),
                any(Object.class), any(Object.class));
    }

    // -------------------------------------------------------------------
    // Defensive null/blank handling
    // -------------------------------------------------------------------

    @Test
    @DisplayName("resolve returns empty for null input without hitting the DB")
    void nullInput_returnsEmpty() {
        Optional<ResolveReceivedDeviceService.DeviceMatch> result = service.resolve(null);

        assertThat(result).isEmpty();
        verify(jdbc, never()).query(anyString(), any(RowMapper.class),
                (Object[]) any());
    }

    @Test
    @DisplayName("resolve returns empty for blank string without hitting the DB")
    void blankInput_returnsEmpty() {
        Optional<ResolveReceivedDeviceService.DeviceMatch> result = service.resolve("   ");

        assertThat(result).isEmpty();
        verify(jdbc, never()).query(anyString(), any(RowMapper.class),
                (Object[]) any());
    }

    @Test
    @DisplayName("RowMapper extracts the four projected columns correctly")
    void rowMapper_extractsColumns() throws Exception {
        ArgumentCaptor<RowMapper<ResolveReceivedDeviceService.DeviceMatch>> mapperCaptor =
                ArgumentCaptor.forClass(RowMapper.class);

        when(jdbc.query(anyString(), mapperCaptor.capture(),
                eq("Galaxy S20"), eq("Galaxy S20")))
                .thenReturn(List.of(new ResolveReceivedDeviceService.DeviceMatch(
                        "ECO-SAMS-S20", "A_R", "Samsung", "Galaxy S20")));

        service.resolve("Galaxy S20");

        ResultSet rs = org.mockito.Mockito.mock(ResultSet.class);
        when(rs.getString("ecoatm_code")).thenReturn("ECO-SAMS-S20");
        when(rs.getString("merged_grade")).thenReturn("A_R");
        when(rs.getString("brand")).thenReturn("Samsung");
        when(rs.getString("model")).thenReturn("Galaxy S20");

        ResolveReceivedDeviceService.DeviceMatch mapped =
                mapperCaptor.getValue().mapRow(rs, 0);

        assertThat(mapped.ecoatmCode()).isEqualTo("ECO-SAMS-S20");
        assertThat(mapped.mergedGrade()).isEqualTo("A_R");
        assertThat(mapped.brand()).isEqualTo("Samsung");
        assertThat(mapped.model()).isEqualTo("Galaxy S20");
    }
}
