package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Spring-context integration test that verifies the
 * {@code @ConditionalOnProperty(name = "snowflake.enabled", havingValue = "true")}
 * gating on the Snowflake sync wiring.
 *
 * <p>When {@code snowflake.enabled=false}, neither
 * {@link AggregatedInventorySnowflakeSyncService} nor its collaborator
 * {@link SnowflakeAggInventoryReader} should be present in the Spring
 * context — both beans must drop out together so the app boots cleanly
 * without any Snowflake credentials configured.
 *
 * <p>Companion to {@code AggregatedInventorySnowflakeSyncServiceIT}, which
 * covers the {@code snowflake.enabled=true} path against a real Postgres DB.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = "snowflake.enabled=false")
class AggregatedInventorySnowflakeSyncServiceDisabledIT {

    @Autowired private ApplicationContext ctx;

    @Test
    @DisplayName("snowflake.enabled=false => sync service and reader beans are absent")
    void serviceBean_notRegistered_whenSnowflakeDisabled() {
        assertThat(ctx.getBeanNamesForType(AggregatedInventorySnowflakeSyncService.class))
                .as("AggregatedInventorySnowflakeSyncService must not be registered when snowflake.enabled=false")
                .isEmpty();

        assertThat(ctx.getBeanNamesForType(SnowflakeAggInventoryReader.class))
                .as("SnowflakeAggInventoryReader must not be registered when snowflake.enabled=false")
                .isEmpty();
    }
}
