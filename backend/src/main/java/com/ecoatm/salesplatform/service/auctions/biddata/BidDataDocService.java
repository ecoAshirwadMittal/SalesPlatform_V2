package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.BidDataDocRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class BidDataDocService {

    private final BidDataDocRepository repo;
    private final Clock clock;

    public BidDataDocService(BidDataDocRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Transactional
    public BidDataDoc getOrCreate(long userId, long buyerCodeId, long weekId) {
        return repo.findByUserIdAndBuyerCodeIdAndWeekId(userId, buyerCodeId, weekId)
            .orElseGet(() -> {
                BidDataDoc doc = new BidDataDoc();
                doc.setUserId(userId);
                doc.setBuyerCodeId(buyerCodeId);
                doc.setWeekId(weekId);
                Instant now = clock.instant();
                doc.setCreatedDate(now);
                doc.setChangedDate(now);
                return repo.save(doc);
            });
    }
}
