package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "po.sync.writer", havingValue = "jdbc")
public class JdbcPurchaseOrderSnowflakeWriter implements PurchaseOrderSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public JdbcPurchaseOrderSnowflakeWriter(
            @Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
    }

    @Override
    public void upsert(PurchaseOrderSnowflakePayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            snowflakeJdbc.update(
                    "CALL AUCTIONS.UPSERT_PURCHASE_ORDER(?, ?, ?, ?, ?, ?)",
                    new Object[] {
                            json,
                            actingUser(),
                            payload.weekFrom().year(),
                            payload.weekFrom().weekNumber(),
                            payload.weekTo().year(),
                            payload.weekTo().weekNumber()
                    });
        } catch (Exception ex) {
            throw new RuntimeException("Failed to call AUCTIONS.UPSERT_PURCHASE_ORDER", ex);
        }
    }

    @Override
    public void delete(long purchaseOrderId) {
        snowflakeJdbc.update("CALL AUCTIONS.DELETE_PURCHASE_ORDER(?)", purchaseOrderId);
    }

    private static String actingUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || auth.getName() == null) ? "Scheduler" : auth.getName();
    }
}
