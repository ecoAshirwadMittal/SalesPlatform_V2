package com.ecoatm.salesplatform.repository.mdm;

import com.ecoatm.salesplatform.model.mdm.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WeekRepository extends JpaRepository<Week, Long> {

    /**
     * Mendix parity: SUB_NavigateToAggregatedInventoryPage retrieves the week
     * whose weekEndDateTime > CurrentDateTime, ordered by start ascending.
     * Returns the first such week so the page opens on the active auction week.
     */
    @Query("SELECT w FROM Week w WHERE w.weekEndDateTime > CURRENT_TIMESTAMP ORDER BY w.weekStartDateTime ASC")
    List<Week> findFutureWeeks();

    List<Week> findAllByOrderByWeekStartDateTimeDesc();

    /**
     * Weeks that have already started (current + past), newest first.
     * Admin inventory dropdown hides not-yet-started weeks because they have
     * no Snowflake data to render.
     */
    @Query("SELECT w FROM Week w WHERE w.weekStartDateTime <= CURRENT_TIMESTAMP ORDER BY w.weekStartDateTime DESC")
    List<Week> findCurrentAndPastWeeks();
}
