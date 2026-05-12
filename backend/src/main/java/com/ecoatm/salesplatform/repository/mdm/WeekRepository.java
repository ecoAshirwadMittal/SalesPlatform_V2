package com.ecoatm.salesplatform.repository.mdm;

import com.ecoatm.salesplatform.model.mdm.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WeekRepository extends JpaRepository<Week, Long> {

    /**
     * Resolves a date to the auction Week containing it
     * ({@code weekStartDateTime <= date < weekEndDateTime}).
     *
     * <p>Backs the partial-credit calc engine — given a
     * {@code CreditRequest.orderCreatedDate}, the engine needs the week
     * the order was placed in so it can query {@code auctions.bid_data}
     * for the max submitted bid on the received device.
     */
    @Query("SELECT w FROM Week w WHERE w.weekStartDateTime <= :date AND w.weekEndDateTime > :date")
    Optional<Week> findByDate(@Param("date") Instant date);

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

    /**
     * Current + future weeks, newest first. Backs the New PO modal: a PO
     * being created should not be allowed to start in the past, so the
     * dropdown should only offer the present week onward.
     *
     * Edge of "current": the current week's end is still in the future
     * (we haven't passed weekEndDateTime yet) → it's included. Past
     * weeks (weekEndDateTime <= now) are excluded.
     */
    @Query("SELECT w FROM Week w WHERE w.weekEndDateTime > CURRENT_TIMESTAMP ORDER BY w.weekStartDateTime DESC")
    List<Week> findCurrentAndFutureWeeksDesc();
}
