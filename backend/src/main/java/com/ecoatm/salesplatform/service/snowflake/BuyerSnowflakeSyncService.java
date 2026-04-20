package com.ecoatm.salesplatform.service.snowflake;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.repository.BuyerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BuyerSnowflakeSyncService {

    private static final Logger log = LoggerFactory.getLogger(BuyerSnowflakeSyncService.class);

    private final BuyerRepository buyerRepository;

    public BuyerSnowflakeSyncService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @Async(AsyncConfig.SNOWFLAKE_EXECUTOR)
    public void syncBuyer(long buyerId) {
        var buyer = buyerRepository.findById(buyerId);
        if (buyer.isEmpty()) {
            log.warn("Snowflake sync skipped: buyer not found buyerId={}", buyerId);
            return;
        }
        doSync(buyer.get());
    }

    @Async(AsyncConfig.SNOWFLAKE_EXECUTOR)
    public void syncAllBuyers() {
        var buyers = buyerRepository.findAll();
        log.info("Snowflake bulk sync started: {} buyers", buyers.size());
        for (Buyer b : buyers) {
            doSync(b);
        }
        log.info("Snowflake bulk sync completed: {} buyers", buyers.size());
    }

    private void doSync(Buyer buyer) {
        // Stubbed — will integrate with Snowflake REST API or JDBC when credentials are available.
        // Mendix exports buyer as XML via ExportXml + ExecuteDatabaseQuery.
        log.info("Snowflake sync: buyerId={} companyName={} status={}",
                buyer.getId(), buyer.getCompanyName(), buyer.getStatus());
    }
}
