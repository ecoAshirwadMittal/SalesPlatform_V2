package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.dto.partialcredit.BuyerCodeOption;
import com.ecoatm.salesplatform.dto.partialcredit.BuyerUserOption;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Unit tests for {@link OnBehalfSubmissionService}. Mocks
 * {@code JdbcTemplate} so the SQL doesn't need a live DB — the dropdown
 * pickers are projection-shaped, the createDraft happy path leans on
 * {@code CreditRequestService.createDraft}.
 */
@ExtendWith(MockitoExtension.class)
class OnBehalfSubmissionServiceTest {

    @Mock private JdbcTemplate jdbc;
    @Mock private CreditRequestService creditRequestService;
    @Mock private CreditRequestRepository creditRequestRepository;

    private OnBehalfSubmissionService service;

    @BeforeEach
    void setUp() {
        service = new OnBehalfSubmissionService(jdbc, creditRequestService, creditRequestRepository);
    }

    @Test
    @DisplayName("listEligibleBuyerCodes — returns whatever the JDBC mapper produces, ordered by code")
    void listEligibleBuyerCodes_passThrough() {
        List<BuyerCodeOption> rows = List.of(
                new BuyerCodeOption(1L, "20399", "Acme Corp"),
                new BuyerCodeOption(2L, "30001", "Beta Industries"));
        when(jdbc.query(anyString(), any(RowMapper.class))).thenReturn(rows);

        List<BuyerCodeOption> actual = service.listEligibleBuyerCodes(7L);

        assertThat(actual).containsExactlyElementsOf(rows);
    }

    @Test
    @DisplayName("listBuyersForCode — propagates the buyerCodeId into the query")
    void listBuyersForCode_passesParam() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(42L)))
                .thenReturn(List.of(new BuyerUserOption(101L, "Nadia", "nadia@example.com")));

        List<BuyerUserOption> actual = service.listBuyersForCode(7L, 42L);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).userId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("createDraftOnBehalf — happy path stamps is_on_behalf + both FKs")
    void createDraftOnBehalf_happyPath() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(42L)))
                .thenReturn(List.of(new BuyerUserOption(101L, "Nadia", "nadia@example.com")));
        CreditRequest draft = new CreditRequest();
        draft.setId(500L);
        when(creditRequestService.createDraft(eq("SO-1"), eq(42L), eq(7L), eq(true))).thenReturn(draft);
        when(creditRequestRepository.save(any(CreditRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        CreditRequest saved = service.createDraftOnBehalf(7L, "SO-1", 42L, 101L);

        assertThat(saved.getIsOnBehalf()).isTrue();
        assertThat(saved.getOnBehalfOfId()).isEqualTo(101L);
        assertThat(saved.getOnBehalfBuyerCodeId()).isEqualTo(42L);
        verify(creditRequestRepository).save(draft);
    }

    @Test
    @DisplayName("createDraftOnBehalf — rejects users not associated with the buyer code (SecurityException)")
    void createDraftOnBehalf_unassociatedUser_throws() {
        // listBuyersForCode returns a different user — 999 is not in the list.
        when(jdbc.query(anyString(), any(RowMapper.class), eq(42L)))
                .thenReturn(List.of(new BuyerUserOption(101L, "Nadia", "nadia@example.com")));

        assertThatThrownBy(() -> service.createDraftOnBehalf(7L, "SO-1", 42L, 999L))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("999");
        verify(creditRequestService, never()).createDraft(any(), any(), any(), anyBoolean());
    }

    @Test
    @DisplayName("createDraftOnBehalf — blank orderNumber rejected (IllegalArgumentException)")
    void createDraftOnBehalf_blankOrder_throws() {
        assertThatThrownBy(() -> service.createDraftOnBehalf(7L, "  ", 42L, 101L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("orderNumber");
    }

    @Test
    @DisplayName("createDraftOnBehalf — null buyerCodeId rejected")
    void createDraftOnBehalf_nullCode_throws() {
        assertThatThrownBy(() -> service.createDraftOnBehalf(7L, "SO-1", null, 101L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("buyerCodeId");
    }

    @Test
    @DisplayName("createDraftOnBehalf — null onBehalfOfUserId rejected")
    void createDraftOnBehalf_nullUser_throws() {
        assertThatThrownBy(() -> service.createDraftOnBehalf(7L, "SO-1", 42L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("onBehalfOfUserId");
    }

    @Test
    @DisplayName("createDraftOnBehalf — calls createDraft with isAdmin=true so buyer-code junction check is bypassed")
    void createDraftOnBehalf_callsCreateDraftAsAdmin() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(42L)))
                .thenReturn(List.of(new BuyerUserOption(101L, "Nadia", "nadia@example.com")));
        CreditRequest draft = new CreditRequest();
        when(creditRequestService.createDraft(any(), any(), any(), eq(true))).thenReturn(draft);
        when(creditRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createDraftOnBehalf(7L, "SO-1", 42L, 101L);

        verify(creditRequestService, times(1))
                .createDraft(eq("SO-1"), eq(42L), eq(7L), eq(true));
    }

    @Test
    @DisplayName("createDraftOnBehalf — selects buyer-belongs-to-code from listBuyersForCode, not a hardcoded set")
    void createDraftOnBehalf_buyerCheckUsesListing() {
        // Multiple eligible buyers — one of which the rep picked.
        List<BuyerUserOption> buyers = new ArrayList<>();
        buyers.add(new BuyerUserOption(101L, "Alice", "alice@example.com"));
        buyers.add(new BuyerUserOption(202L, "Bob", "bob@example.com"));
        buyers.add(new BuyerUserOption(303L, "Carol", "carol@example.com"));
        when(jdbc.query(anyString(), any(RowMapper.class), eq(42L))).thenReturn(buyers);
        CreditRequest draft = new CreditRequest();
        when(creditRequestService.createDraft(any(), any(), any(), anyBoolean())).thenReturn(draft);
        when(creditRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Each of the three eligible users should be accepted.
        for (Long userId : List.of(101L, 202L, 303L)) {
            service.createDraftOnBehalf(7L, "SO-1", 42L, userId);
        }

        // The fourth one outside the list must be rejected.
        assertThatThrownBy(() -> service.createDraftOnBehalf(7L, "SO-1", 42L, 404L))
                .isInstanceOf(SecurityException.class);
    }
}
