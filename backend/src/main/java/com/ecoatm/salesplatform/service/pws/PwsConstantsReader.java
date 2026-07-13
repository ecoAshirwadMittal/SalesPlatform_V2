package com.ecoatm.salesplatform.service.pws;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Reads the {@code pws.pws_constants} singleton (Mendix
 * {@code EcoATM_PWS.PWSConstants}) for the counter-offer-reminder job. Uses
 * {@link JdbcTemplate} rather than a JPA entity to match how
 * {@code PWSAdminController.getPWSConstants} already reads this table — there is
 * no {@code PwsConstants} entity in the model, and introducing one purely for a
 * four-column config read would be heavier than the read warrants.
 *
 * <p>Isolating the read behind this component keeps
 * {@link CounterOfferReminderService} unit-testable with a mocked reader instead
 * of a mocked {@code JdbcTemplate} + hand-rolled {@code RowMapper} stubbing.
 */
@Component
public class PwsConstantsReader {

    /** Fail-safe when the singleton row is absent: send nothing rather than
     *  spam without configuration. Both toggles off, both thresholds null. */
    static final PwsCounterReminderSettings DISABLED =
            new PwsCounterReminderSettings(false, false, null, null);

    private final JdbcTemplate jdbc;

    public PwsConstantsReader(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Loads the counter-offer-reminder toggles + hour thresholds from the
     * singleton row. Returns {@link #DISABLED} (send nothing) when no row
     * exists, so a mis-seeded environment fails closed.
     */
    public PwsCounterReminderSettings loadCounterReminderSettings() {
        List<PwsCounterReminderSettings> rows = jdbc.query(
                """
                SELECT send_first_reminder,
                       send_second_reminder,
                       hours_first_counter_reminder,
                       hours_second_counter_reminder
                FROM pws.pws_constants
                ORDER BY id
                LIMIT 1
                """,
                (rs, rowNum) -> new PwsCounterReminderSettings(
                        rs.getBoolean("send_first_reminder"),
                        rs.getBoolean("send_second_reminder"),
                        nullableInt(rs, "hours_first_counter_reminder"),
                        nullableInt(rs, "hours_second_counter_reminder")));
        return rows.isEmpty() ? DISABLED : rows.get(0);
    }

    /** {@code ResultSet.getInt} returns 0 for SQL NULL; distinguish a real 0
     *  from an absent threshold via {@link ResultSet#wasNull()} so the null
     *  second-threshold branch of the legacy decision tree stays reachable. */
    private static Integer nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
