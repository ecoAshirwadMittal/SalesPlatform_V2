package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.CompanyHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Set;

public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, Long> {

    /**
     * The holiday dates within {@code [start, end]} inclusive, as a set for O(1)
     * membership tests during the SLA business-day back-walk. The service fetches
     * a generously-sized window once so the walk never runs past the loaded set.
     */
    @Query("SELECT ch.holidayDate FROM CompanyHoliday ch "
            + "WHERE ch.holidayDate BETWEEN :start AND :end")
    Set<LocalDate> findHolidayDatesBetween(@Param("start") LocalDate start,
                                           @Param("end") LocalDate end);
}
