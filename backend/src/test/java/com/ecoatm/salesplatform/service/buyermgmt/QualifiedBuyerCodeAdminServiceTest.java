package com.ecoatm.salesplatform.service.buyermgmt;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodePageResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeRow;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QualifiedBuyerCodeAdminServiceTest {

    @Mock private EntityManager em;
    @Mock private QualifiedBuyerCodeRepository repository;

    private QualifiedBuyerCodeAdminService service;

    @BeforeEach
    void setUp() {
        service = new QualifiedBuyerCodeAdminService(em, repository);
    }

    @Test
    void list_returnsPagedRows() {
        Query selectQ = mock(Query.class);
        Query countQ = mock(Query.class);
        when(em.createNativeQuery(anyString()))
                .thenReturn(selectQ)
                .thenReturn(countQ);
        when(selectQ.setParameter(anyString(), any())).thenReturn(selectQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
        when(selectQ.getResultList()).thenReturn(Collections.singletonList(sampleRow(1L, 10L, 55L)));
        when(countQ.getSingleResult()).thenReturn(1L);

        QualifiedBuyerCodePageResponse resp = service.list(null, null, 0, 20);

        assertThat(resp.totalElements()).isEqualTo(1);
        assertThat(resp.content()).hasSize(1);
        assertThat(resp.content().get(0).id()).isEqualTo(1L);
        assertThat(resp.content().get(0).qualificationType()).isEqualTo("Not_Qualified");
    }

    @Test
    void qualify_setsManualQualificationType() {
        QualifiedBuyerCode qbc = qbcEntity(77L, QualificationType.Not_Qualified);
        when(repository.findById(77L)).thenReturn(Optional.of(qbc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        QualifiedBuyerCodeRow result = service.qualify(77L);

        assertThat(result.qualificationType()).isEqualTo("Manual");
        verify(repository).save(qbc);
    }

    @Test
    void qualify_notFound_throwsEntityNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.qualify(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private static Object[] sampleRow(long id, long saId, long buyerCodeId) {
        return new Object[]{
                id, saId, buyerCodeId, "Not_Qualified",
                false, false, null,
                false,
                java.sql.Timestamp.valueOf(LocalDateTime.now()),
                java.sql.Timestamp.valueOf(LocalDateTime.now())
        };
    }

    private static QualifiedBuyerCode qbcEntity(long id, QualificationType qt) {
        QualifiedBuyerCode qbc = new QualifiedBuyerCode();
        qbc.setId(id);
        qbc.setSchedulingAuctionId(10L);
        qbc.setBuyerCodeId(55L);
        qbc.setQualificationType(qt);
        qbc.setCreatedDate(LocalDateTime.now());
        qbc.setChangedDate(LocalDateTime.now());
        return qbc;
    }
}
