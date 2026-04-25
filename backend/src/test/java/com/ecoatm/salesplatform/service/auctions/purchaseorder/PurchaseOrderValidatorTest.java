package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseOrderValidatorTest {

    WeekRepository weekRepo;
    BuyerCodeRepository bcRepo;
    PurchaseOrderValidator validator;

    @BeforeEach
    void init() {
        weekRepo = mock(WeekRepository.class);
        bcRepo = mock(BuyerCodeRepository.class);
        validator = new PurchaseOrderValidator(weekRepo, bcRepo);
    }

    @Test
    void weekRangeOkPasses() {
        Week from = stubWeek(1L, 202501);
        Week to = stubWeek(2L, 202504);
        when(weekRepo.findById(1L)).thenReturn(Optional.of(from));
        when(weekRepo.findById(2L)).thenReturn(Optional.of(to));
        var resolved = validator.resolveWeekRange(1L, 2L);
        assertThat(resolved.from().getId()).isEqualTo(1L);
        assertThat(resolved.to().getId()).isEqualTo(2L);
    }

    @Test
    void weekRangeReversedThrows() {
        Week from = stubWeek(1L, 202504);
        Week to = stubWeek(2L, 202501);
        when(weekRepo.findById(1L)).thenReturn(Optional.of(from));
        when(weekRepo.findById(2L)).thenReturn(Optional.of(to));
        assertThatThrownBy(() -> validator.resolveWeekRange(1L, 2L))
                .isInstanceOf(PurchaseOrderValidationException.class)
                .hasMessageContaining("week_from must be <= week_to");
    }

    @Test
    void weekIdNotFoundThrows() {
        when(weekRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> validator.resolveWeekRange(99L, 100L))
                .isInstanceOf(PurchaseOrderValidationException.class);
    }

    @Test
    void buyerCodesAllExistPasses() {
        when(bcRepo.findCodesIn(List.of("ABC", "DEF"))).thenReturn(List.of("ABC", "DEF"));
        validator.requireBuyerCodes(List.of("ABC", "DEF"));
    }

    @Test
    void missingBuyerCodesThrowsListingOffenders() {
        when(bcRepo.findCodesIn(List.of("ABC", "MISSING"))).thenReturn(List.of("ABC"));
        assertThatThrownBy(() -> validator.requireBuyerCodes(List.of("ABC", "MISSING")))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex -> {
                    assertThat(ex.getCode()).isEqualTo("MISSING_BUYER_CODE");
                    assertThat(ex.getDetails()).contains("MISSING");
                });
    }

    private static Week stubWeek(long id, int weekId) {
        Week w = new Week();
        try {
            var idField = Week.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(w, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        w.setWeekId(weekId);
        return w;
    }
}
