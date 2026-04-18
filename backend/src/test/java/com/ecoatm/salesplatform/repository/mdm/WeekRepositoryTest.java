package com.ecoatm.salesplatform.repository.mdm;

import com.ecoatm.salesplatform.model.mdm.Week;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class WeekRepositoryTest {

    @Autowired private WeekRepository weekRepository;

    @Test
    @DisplayName("findAll returns at least one week from V65 seed")
    void findAll_afterFlywaySeed_returnsRows() {
        assertThat(weekRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("findCurrentWeek returns the week whose end datetime is in the future")
    void findCurrentWeek_returnsOne() {
        Optional<Week> current = weekRepository.findCurrentWeek();
        assertThat(current).isPresent();
        assertThat(current.get().getWeekEndDateTime()).isAfter(java.time.Instant.now());
    }
}
