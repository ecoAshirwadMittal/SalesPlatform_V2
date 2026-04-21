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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class Round1InitializationService {

    private static final Logger log = LoggerFactory.getLogger(Round1InitializationService.class);

    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;
    private final AggregatedInventoryRepository aggInvRepo;
    private final QualifiedBuyerCodeRepository qbcRepo;
    private final BuyerCodeRepository buyerCodeRepo;
    private final AuctionsFeatureConfigService configService;

    public Round1InitializationService(
            SchedulingAuctionRepository saRepo,
            AuctionRepository auctionRepo,
            AggregatedInventoryRepository aggInvRepo,
            QualifiedBuyerCodeRepository qbcRepo,
            BuyerCodeRepository buyerCodeRepo,
            AuctionsFeatureConfigService configService) {
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
        this.aggInvRepo = aggInvRepo;
        this.qbcRepo = qbcRepo;
        this.buyerCodeRepo = buyerCodeRepo;
        this.configService = configService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public Round1InitializationResult initialize(Long schedulingAuctionId) {
        long startedAt = System.currentTimeMillis();

        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
                .orElseThrow(() -> new SchedulingAuctionNotFoundException(schedulingAuctionId));
        Auction auction = auctionRepo.findById(sa.getAuctionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Auction not found for scheduling_auction id=" + schedulingAuctionId
                                + " auctionId=" + sa.getAuctionId()));
        Long weekId = auction.getWeekId();

        AuctionsFeatureConfig config = configService.getOrCreate();
        BigDecimal minBid = config.getMinimumAllowedBid();

        int clampedNonDw = aggInvRepo.clampNonDwTargetPrice(weekId, minBid);
        int clampedDw = aggInvRepo.clampDwTargetPrice(weekId, minBid);

        qbcRepo.deleteBySchedulingAuctionId(schedulingAuctionId);

        List<BuyerCode> buyerCodes = buyerCodeRepo.findActiveWholesaleOrDataWipe();
        List<QualifiedBuyerCode> qbcs = new ArrayList<>(buyerCodes.size());
        for (BuyerCode bc : buyerCodes) {
            QualifiedBuyerCode qbc = new QualifiedBuyerCode();
            qbc.setSchedulingAuctionId(schedulingAuctionId);
            qbc.setBuyerCodeId(bc.getId());
            qbc.setQualificationType(QualificationType.Qualified);
            qbc.setIncluded(true);
            qbc.setSpecialTreatment(false);
            qbcs.add(qbc);
        }
        // saveAll + hibernate.jdbc.batch_size=50 batches the ~579 inserts into
        // ~12 round-trips instead of 579, per the post-review performance note.
        qbcRepo.saveAll(qbcs);

        long durationMs = System.currentTimeMillis() - startedAt;
        log.info("r1-init completed auctionId={} schedulingAuctionId={} weekId={} "
                        + "clampedNonDw={} clampedDw={} qbcsCreated={} durationMs={}",
                auction.getId(), schedulingAuctionId, weekId,
                clampedNonDw, clampedDw, buyerCodes.size(), durationMs);

        return new Round1InitializationResult(
                schedulingAuctionId,
                auction.getId(),
                weekId,
                clampedNonDw,
                clampedDw,
                buyerCodes.size(),
                durationMs);
    }
}
