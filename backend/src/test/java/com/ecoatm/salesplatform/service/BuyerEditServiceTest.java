package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerCodeUpsertRequest;
import com.ecoatm.salesplatform.dto.BuyerDetailResponse;
import com.ecoatm.salesplatform.dto.BuyerUpsertRequest;
import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.BuyerRepository;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.ecoatm.salesplatform.service.snowflake.BuyerSnowflakeEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerEditServiceTest {

    @Mock private BuyerRepository buyerRepository;
    @Mock private BuyerCodeRepository buyerCodeRepository;
    @Mock private SalesRepresentativeRepository salesRepRepository;
    @Mock private EntityManager em;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private Authentication adminAuth;
    @Mock private Authentication complianceAuth;
    @Mock private Query nativeQuery;

    private BuyerEditService service;

    @BeforeEach
    void setUp() {
        service = new BuyerEditService(buyerRepository, buyerCodeRepository, salesRepRepository, em, eventPublisher);

        Collection<GrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_Administrator"));
        Collection<GrantedAuthority> complianceAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_Compliance"));

        lenient().doReturn(adminAuthorities).when(adminAuth).getAuthorities();
        lenient().doReturn(complianceAuthorities).when(complianceAuth).getAuthorities();
    }

    private Buyer makeBuyer(Long id, String name, BuyerStatus status) {
        Buyer b = new Buyer();
        b.setId(id);
        b.setCompanyName(name);
        b.setStatus(status);
        b.setSpecialBuyer(false);
        return b;
    }

    private BuyerCode makeCode(Long id, String code, String type, Integer budget) {
        BuyerCode bc = new BuyerCode();
        bc.setId(id);
        bc.setCode(code);
        bc.setBuyerCodeType(type);
        bc.setBudget(budget);
        bc.setSoftDelete(false);
        return bc;
    }

    private SalesRepresentative makeRep(Long id, String first, String last) {
        SalesRepresentative sr = new SalesRepresentative();
        sr.setId(id);
        sr.setFirstName(first);
        sr.setLastName(last);
        sr.setActive(true);
        return sr;
    }

    @Nested
    @DisplayName("get()")
    class GetTests {

        @Test
        @DisplayName("returns detail with admin permissions")
        void get_admin_returnsFullPermissions() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(salesRepRepository.findByBuyerId(1L))
                    .thenReturn(List.of(makeRep(10L, "John", "Doe")));
            when(buyerCodeRepository.findByBuyerId(1L))
                    .thenReturn(List.of(makeCode(100L, "AC001", "Wholesale", 5000)));
            when(buyerCodeRepository.existsByCodeIgnoreCaseAndNotSoftDeleted("AC001", 100L))
                    .thenReturn(false);

            BuyerDetailResponse detail = service.get(1L, adminAuth);

            assertThat(detail.id()).isEqualTo(1L);
            assertThat(detail.companyName()).isEqualTo("Acme");
            assertThat(detail.status()).isEqualTo(BuyerStatus.Active);
            assertThat(detail.salesReps()).hasSize(1);
            assertThat(detail.salesReps().get(0).firstName()).isEqualTo("John");
            assertThat(detail.buyerCodes()).hasSize(1);
            assertThat(detail.buyerCodes().get(0).code()).isEqualTo("AC001");
            assertThat(detail.buyerCodes().get(0).codeUniqueValid()).isTrue();
            assertThat(detail.permissions().canEditSalesRep()).isTrue();
            assertThat(detail.permissions().canToggleStatus()).isTrue();
        }

        @Test
        @DisplayName("returns detail with compliance permissions")
        void get_compliance_returnsRestrictedPermissions() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            BuyerDetailResponse detail = service.get(1L, complianceAuth);

            assertThat(detail.permissions().canEditSalesRep()).isFalse();
            assertThat(detail.permissions().canToggleStatus()).isFalse();
            assertThat(detail.permissions().canEditBuyerCodeType()).isFalse();
        }

        @Test
        @DisplayName("throws when buyer not found")
        void get_notFound_throws() {
            when(buyerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.get(999L, adminAuth))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("normalizes null status to Disabled")
        void get_nullStatus_normalizesToDisabled() {
            Buyer buyer = makeBuyer(1L, "Legacy", null);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            BuyerDetailResponse detail = service.get(1L, adminAuth);

            assertThat(detail.status()).isEqualTo(BuyerStatus.Disabled);
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("creates buyer with Active status and returns detail")
        void create_setsActiveStatus() {
            Buyer savedBuyer = makeBuyer(99L, "NewCo", BuyerStatus.Active);
            savedBuyer.setSpecialBuyer(true);
            when(buyerRepository.save(any())).thenReturn(savedBuyer);
            when(salesRepRepository.findByBuyerId(99L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(99L)).thenReturn(List.of());

            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "NewCo", null, true, null, null);

            BuyerDetailResponse result = service.create(req, adminAuth);

            assertThat(result.id()).isEqualTo(99L);
            assertThat(result.companyName()).isEqualTo("NewCo");
            assertThat(result.status()).isEqualTo(BuyerStatus.Active);
            assertThat(result.isSpecialBuyer()).isTrue();
            assertThat(result.permissions().canEditSalesRep()).isTrue();
            verify(eventPublisher).publishEvent(any(BuyerSnowflakeEvent.BuyerSaved.class));
        }

        @Test
        @DisplayName("creates buyer with sales reps and buyer codes")
        void create_withAssociations() {
            Buyer savedBuyer = makeBuyer(99L, "NewCo", BuyerStatus.Active);
            BuyerCode savedCode = makeCode(200L, "NC001", "Wholesale", 1000);
            when(buyerRepository.save(any())).thenReturn(savedBuyer);
            when(buyerCodeRepository.save(any())).thenReturn(savedCode);
            when(salesRepRepository.findByBuyerId(99L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(99L)).thenReturn(List.of(savedCode));
            when(buyerCodeRepository.existsByCodeIgnoreCaseAndNotSoftDeleted(anyString(), anyLong()))
                    .thenReturn(false);

            when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(1);

            BuyerCodeUpsertRequest codeReq = new BuyerCodeUpsertRequest(
                    null, "NC001", "Wholesale", 1000, false);
            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "NewCo", null, false, List.of(10L), List.of(codeReq));

            BuyerDetailResponse result = service.create(req, adminAuth);

            assertThat(result.buyerCodes()).hasSize(1);
            assertThat(result.buyerCodes().get(0).code()).isEqualTo("NC001");
        }
    }

    @Nested
    @DisplayName("toggleStatus()")
    class ToggleStatusTests {

        @Test
        @DisplayName("toggles Active to Disabled when no active users")
        void toggle_activeToDisabled_succeeds() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(buyerRepository.save(any())).thenReturn(buyer);
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
            when(nativeQuery.getSingleResult()).thenReturn(0L);

            BuyerDetailResponse result = service.toggleStatus(1L, adminAuth);

            assertThat(result.status()).isEqualTo(BuyerStatus.Disabled);
            verify(eventPublisher).publishEvent(any(BuyerSnowflakeEvent.BuyerSaved.class));
        }

        @Test
        @DisplayName("toggles Disabled to Active without guard check")
        void toggle_disabledToActive_succeeds() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Disabled);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(buyerRepository.save(any())).thenReturn(buyer);
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            BuyerDetailResponse result = service.toggleStatus(1L, adminAuth);

            assertThat(result.status()).isEqualTo(BuyerStatus.Active);
        }

        @Test
        @DisplayName("toggle to Disabled blocked by active users")
        void toggle_disableBlockedByActiveUsers() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));

            when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
            when(nativeQuery.getSingleResult()).thenReturn(5L);

            assertThatThrownBy(() -> service.toggleStatus(1L, adminAuth))
                    .isInstanceOf(BuyerEditService.BuyerDisableException.class)
                    .hasMessageContaining("5 active user(s)");
        }

        @Test
        @DisplayName("throws when buyer not found")
        void toggle_notFound_throws() {
            when(buyerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.toggleStatus(999L, adminAuth))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("compliance user cannot change status")
        void update_compliance_statusIgnored() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(buyerRepository.save(any())).thenReturn(buyer);
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "Acme Updated", BuyerStatus.Disabled, false, null, null);

            BuyerDetailResponse result = service.update(1L, req, complianceAuth);

            assertThat(result.status()).isEqualTo(BuyerStatus.Active);
            verify(eventPublisher).publishEvent(any(BuyerSnowflakeEvent.BuyerSaved.class));
        }

        @Test
        @DisplayName("admin can update status to Disabled when no active users assigned")
        void update_admin_disableSuccess() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(buyerRepository.save(any())).thenReturn(buyer);
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
            when(nativeQuery.getSingleResult()).thenReturn(0L);

            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "Acme", BuyerStatus.Disabled, false, null, null);

            BuyerDetailResponse result = service.update(1L, req, adminAuth);

            assertThat(result.status()).isEqualTo(BuyerStatus.Disabled);
        }

        @Test
        @DisplayName("admin cannot disable buyer with active users assigned")
        void update_admin_disableBlockedByActiveUsers() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));

            when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
            when(nativeQuery.getSingleResult()).thenReturn(3L);

            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "Acme", BuyerStatus.Disabled, false, null, null);

            assertThatThrownBy(() -> service.update(1L, req, adminAuth))
                    .isInstanceOf(BuyerEditService.BuyerDisableException.class)
                    .hasMessageContaining("3 active user(s)");
        }

        @Test
        @DisplayName("compliance user can update buyer codes")
        void update_compliance_canUpdateCodes() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            BuyerCode existingCode = makeCode(100L, "AC001", "Wholesale", 5000);

            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(buyerRepository.save(any())).thenReturn(buyer);
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of(existingCode));
            when(buyerCodeRepository.save(any())).thenReturn(existingCode);
            when(buyerCodeRepository.existsByCodeIgnoreCaseAndNotSoftDeleted(anyString(), anyLong()))
                    .thenReturn(false);
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());

            BuyerCodeUpsertRequest codeReq = new BuyerCodeUpsertRequest(
                    100L, "AC001", "Data_Wipe", 7000, false);
            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "Acme", null, false, null, List.of(codeReq));

            BuyerDetailResponse result = service.update(1L, req, complianceAuth);

            assertThat(result.buyerCodes()).hasSize(1);
            // Compliance cannot change type — should stay Wholesale
            assertThat(existingCode.getBuyerCodeType()).isEqualTo("Wholesale");
            assertThat(existingCode.getBudget()).isEqualTo(7000);
        }

        @Test
        @DisplayName("compliance user's sales rep changes are ignored")
        void update_compliance_salesRepIgnored() {
            Buyer buyer = makeBuyer(1L, "Acme", BuyerStatus.Active);
            when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
            when(buyerRepository.save(any())).thenReturn(buyer);
            when(salesRepRepository.findByBuyerId(1L)).thenReturn(List.of());
            when(buyerCodeRepository.findByBuyerId(1L)).thenReturn(List.of());

            BuyerUpsertRequest req = new BuyerUpsertRequest(
                    "Acme", null, false, List.of(10L, 20L), null);

            service.update(1L, req, complianceAuth);

            verify(em, never()).createNativeQuery("DELETE FROM buyer_mgmt.buyer_sales_reps WHERE buyer_id = :buyerId");
        }
    }
}
