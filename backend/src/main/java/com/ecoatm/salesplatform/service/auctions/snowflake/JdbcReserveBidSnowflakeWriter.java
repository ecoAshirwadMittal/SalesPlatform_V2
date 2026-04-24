package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConditionalOnProperty(value = "eb.sync.writer", havingValue = "jdbc", matchIfMissing = false)
public class JdbcReserveBidSnowflakeWriter implements ReserveBidSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(JdbcReserveBidSnowflakeWriter.class);

    private final JdbcTemplate snowflakeJdbc;
    private final ObjectMapper jsonMapper;

    public JdbcReserveBidSnowflakeWriter(@Qualifier("snowflakeDataSource") DataSource snowflakeDataSource) {
        this.snowflakeJdbc = new JdbcTemplate(snowflakeDataSource);
        this.jsonMapper = new ObjectMapper();
    }

    @Override
    public int upsert(ReserveBidSnowflakePayload payload) {
        return callStoredProc("UPSERT", payload);
    }

    @Override
    public int delete(ReserveBidSnowflakePayload payload) {
        return callStoredProc("DELETE", payload);
    }

    private int callStoredProc(String action, ReserveBidSnowflakePayload payload) {
        try {
            String json = jsonMapper.writeValueAsString(payload.rows());
            snowflakeJdbc.update("CALL AUCTIONS.UPSERT_RESERVE_BID(?, ?)",
                    json, payload.actingUser());
            log.info("[eb-jdbc-writer] {} ok rows={} user={}", action, payload.rows().size(), payload.actingUser());
            return payload.rows().size();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Snowflake payload JSON encoding failed", ex);
        }
    }
}
