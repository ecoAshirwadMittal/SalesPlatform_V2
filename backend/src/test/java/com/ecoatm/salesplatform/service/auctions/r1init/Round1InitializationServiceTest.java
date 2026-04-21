package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.buyermgmt.AuctionsFeatureConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Round1InitializationServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock AuctionRepository auctionRepo;
    @Mock AggregatedInventoryRepository aggInvRepo;
    @Mock QualifiedBuyerCodeRepository qbcRepo;
    @Mock BuyerCodeRepository buyerCodeRepo;
    @Mock AuctionsFeatureConfigService configService;

    Round1InitializationService service;

    private static final long SA_ID = 301L;
    private static final long AUCTION_ID = 101L;
    private static final long WEEK_ID = 42L;
    private static final BigDecimal MIN_BID = new BigDecimal("2.00");

    @BeforeEach
    void setUp() {
        service = new Round1InitializationService(
                saRepo, auctionRepo, aggInvRepo, qbcRepo, buyerCodeRepo, configService);
    }

    @Test
    void initialize_happyPath_clampsBothAndCreatesQbcs() {
        stubSaAndAuction();
        stubConfig();
        when(aggInvRepo.clampNonDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(5);
        when(aggInvRepo.clampDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(3);
        when(qbcRepo.deleteBySchedulingAuctionId(SA_ID)).thenReturn(0);
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe())
                .thenReturn(List.of(buyerCode(10L), buyerCode(11L), buyerCode(12L)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));

        Round1InitializationResult result = service.initialize(SA_ID);

        assertThat(result.clampedNonDw()).isEqualTo(5);
        assertThat(result.clampedDw()).isEqualTo(3);
        assertThat(result.qbcsCreated()).isEqualTo(3);
        assertThat(result.auctionId()).isEqualTo(AUCTION_ID);
        assertThat(result.weekId()).isEqualTo(WEEK_ID);

        ArgumentCaptor<QualifiedBuyerCode> qbcCaptor = ArgumentCaptor.forClass(QualifiedBuyerCode.class);
        verify(qbcRepo, org.mockito.Mockito.times(3)).save(qbcCaptor.capture());
        assertThat(qbcCaptor.getAllValues()).allSatisfy(q -> {
            assertThat(q.getSchedulingAuctionId()).isEqualTo(SA_ID);
            assertThat(q.getQualificationType()).isEqualTo(QualificationType.Qualified);
            assertThat(q.isIncluded()).isTrue();
            assertThat(q.isSpecialTreatment()).isFalse();
        });
        assertThat(qbcCaptor.getAllValues())
                .extracting(QualifiedBuyerCode::getBuyerCodeId)
                .containsExactlyInAnyOrder(10L, 11L, 12L);
    }

    @Test
    void initialize_emptyBuyerList_createsZeroQbcsAndDoesNotThrow() {
        stubSaAndAuction();
        stubConfig();
        when(aggInvRepo.clampNonDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(aggInvRepo.clampDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(qbcRepo.deleteBySchedulingAuctionId(SA_ID)).thenReturn(7);
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe()).thenReturn(List.of());

        Round1InitializationResult result = service.initialize(SA_ID);

        assertThat(result.qbcsCreated()).isZero();
        verify(qbcRepo, never()).save(any());
    }

    @Test
    void initialize_missingSchedulingAuction_throws() {
        when(saRepo.findById(SA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.initialize(SA_ID))
                .isInstanceOf(SchedulingAuctionNotFoundException.class)
                .hasMessageContaining(String.valueOf(SA_ID));

        verify(aggInvRepo, never()).clampNonDwTargetPrice(any(), any());
    }

    @Test
    void initialize_nothingBelowFloor_stillRewritesQbcs() {
        stubSaAndAuction();
        stubConfig();
        when(aggInvRepo.clampNonDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(aggInvRepo.clampDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(qbcRepo.deleteBySchedulingAuctionId(SA_ID)).thenReturn(0);
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe())
                .thenReturn(List.of(buyerCode(10L)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));

        Round1InitializationResult result = service.initialize(SA_ID);

        assertThat(result.clampedNonDw()).isZero();
        assertThat(result.clampedDw()).isZero();
        assertThat(result.qbcsCreated()).isEqualTo(1);
        verify(qbcRepo).deleteBySchedulingAuctionId(SA_ID);
    }

    @Test
    void initialize_callsServicesInExpectedOrder() {
        stubSaAndAuction();
        stubConfig();
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe()).thenReturn(List.of());

        service.initialize(SA_ID);

        org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(
                aggInvRepo, qbcRepo, buyerCodeRepo);
        inOrder.verify(aggInvRepo).clampNonDwTargetPrice(WEEK_ID, MIN_BID);
        inOrder.verify(aggInvRepo).clampDwTargetPrice(WEEK_ID, MIN_BID);
        inOrder.verify(qbcRepo).deleteBySchedulingAuctionId(SA_ID);
        inOrder.verify(buyerCodeRepo).findActiveWholesaleOrDataWipe();
    }

    private void stubSaAndAuction() {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(SA_ID);
        sa.setAuctionId(AUCTION_ID);
        sa.setRound(1);
        when(saRepo.findById(SA_ID)).thenReturn(Optional.of(sa));

        Auction auction = new Auction();
        auction.setId(AUCTION_ID);
        auction.setWeekId(WEEK_ID);
        when(auctionRepo.findById(AUCTION_ID)).thenReturn(Optional.of(auction));
    }

    private void stubConfig() {
        AuctionsFeatureConfig config = new AuctionsFeatureConfig();
        config.setMinimumAllowedBid(MIN_BID);
        when(configService.getOrCreate()).thenReturn(config);
    }

    private BuyerCode buyerCode(long id) {
        BuyerCode bc = new BuyerCode();
        bc.setId(id);
        return bc;
    }
}
