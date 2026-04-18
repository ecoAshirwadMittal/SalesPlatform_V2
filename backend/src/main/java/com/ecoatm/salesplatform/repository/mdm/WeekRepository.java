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

    default Optional<Week> findCurrentWeek() {
        return findFutureWeeks().stream().findFirst();
    }

    List<Week> findAllByOrderByWeekStartDateTimeDesc();
}
