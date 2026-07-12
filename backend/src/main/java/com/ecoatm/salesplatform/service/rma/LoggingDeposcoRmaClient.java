package com.ecoatm.salesplatform.service.rma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Default {@link DeposcoRmaClient} — active unless {@code rma.deposco-sync.client}
 * names a real client (e.g. {@code http}). Logs the intended poll at INFO and
 * always reports "no update" ({@link Optional#empty()}), so the scheduled sync
 * job is fully wired but inert until a real Deposco reverse-logistics client
 * lands.
 *
 * <p>Mirrors the {@code Logging*Writer} / {@link
 * com.ecoatm.salesplatform.service.email.LoggingEmailSender} default-bean idiom
 * ({@code @ConditionalOnProperty(..., matchIfMissing = true)}).
 */
@Component
@ConditionalOnProperty(name = "rma.deposco-sync.client", havingValue = "logging", matchIfMissing = true)
public class LoggingDeposcoRmaClient implements DeposcoRmaClient {

    private static final Logger log = LoggerFactory.getLogger(LoggingDeposcoRmaClient.class);

    @Override
    public Optional<DeposcoRmaStatus> fetchStatus(String oracleNumber) {
        log.info("[LoggingDeposcoRmaClient] would poll Deposco for RMA oracleNumber={} — "
                + "no real reverse-logistics endpoint wired; reporting no update", oracleNumber);
        return Optional.empty();
    }
}
