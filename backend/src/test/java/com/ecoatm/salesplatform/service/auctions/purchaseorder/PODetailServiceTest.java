package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PODetailUploadResult;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PODetailServiceTest {

    PurchaseOrderRepository poRepo;
    PODetailRepository detailRepo;
    BuyerCodeRepository buyerCodeRepo;
    POExcelParser parser;
    PurchaseOrderValidator validator;
    ApplicationEventPublisher events;
    PODetailService service;

    @BeforeEach
    void init() {
        poRepo = mock(PurchaseOrderRepository.class);
        detailRepo = mock(PODetailRepository.class);
        buyerCodeRepo = mock(BuyerCodeRepository.class);
        parser = mock(POExcelParser.class);
        validator = mock(PurchaseOrderValidator.class);
        events = mock(ApplicationEventPublisher.class);
        service = new PODetailService(poRepo, detailRepo, buyerCodeRepo,
                parser, validator, events);
    }

    @Test
    void uploadWipeAndReplaceHappyPath() {
        PurchaseOrder po = stubPo(7L);
        BuyerCode abc = stubBuyerCode(11L, "ABC");
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        when(parser.parse(any())).thenReturn(List.of(
                new POExcelParser.ParsedRow(2, "100", "A_YYY", "iPhone",
                        new BigDecimal("10"), 100, "ABC")));
        when(buyerCodeRepo.findByCodeIn(List.of("ABC")))
                .thenReturn(List.of(abc));
        when(detailRepo.deleteAllByPurchaseOrderId(7L)).thenReturn(3);
        when(detailRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        PODetailUploadResult result = service.upload(7L, new ByteArrayInputStream(new byte[]{1}));

        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.deletedCount()).isEqualTo(3);
        assertThat(result.errors()).isEmpty();
        verify(events).publishEvent(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));
    }

    @Test
    void uploadMissingBuyerCodeRejectsEntireUpload() {
        PurchaseOrder po = stubPo(7L);
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        when(parser.parse(any())).thenReturn(List.of(
                new POExcelParser.ParsedRow(2, "100", "A_YYY", "X",
                        new BigDecimal("10"), null, "MISSING")));
        doThrow(new PurchaseOrderValidationException("MISSING_BUYER_CODE",
                "Unknown buyer codes referenced: MISSING", List.of("MISSING")))
            .when(validator).requireBuyerCodes(List.of("MISSING"));

        assertThatThrownBy(() -> service.upload(7L, new ByteArrayInputStream(new byte[]{1})))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("MISSING_BUYER_CODE"));
        verify(detailRepo, never()).deleteAllByPurchaseOrderId(anyLong());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void uploadDuplicateRowsInSheetSurfacedAsSkipped() {
        PurchaseOrder po = stubPo(7L);
        BuyerCode abc = stubBuyerCode(11L, "ABC");
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        when(parser.parse(any())).thenReturn(List.of(
                new POExcelParser.ParsedRow(2, "100", "A_YYY", "X",
                        new BigDecimal("10"), null, "ABC"),
                new POExcelParser.ParsedRow(3, "100", "A_YYY", "X",
                        new BigDecimal("12"), null, "ABC")));
        when(buyerCodeRepo.findByCodeIn(List.of("ABC")))
                .thenReturn(List.of(abc));
        when(detailRepo.deleteAllByPurchaseOrderId(7L)).thenReturn(0);
        when(detailRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        PODetailUploadResult result = service.upload(7L, new ByteArrayInputStream(new byte[]{1}));

        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).reason()).contains("DUPLICATE_IN_SHEET");
    }

    @Test
    void uploadAgainstUnknownPoThrows() {
        when(poRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.upload(99L, new ByteArrayInputStream(new byte[]{1})))
                .isInstanceOfSatisfying(PurchaseOrderException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("PURCHASE_ORDER_NOT_FOUND"));
    }

    private static PurchaseOrder stubPo(long id) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return po;
    }

    private static BuyerCode stubBuyerCode(long id, String code) {
        BuyerCode bc = new BuyerCode();
        try {
            var f = BuyerCode.class.getDeclaredField("id");
            f.setAccessible(true); f.set(bc, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        bc.setCode(code);
        return bc;
    }
}
