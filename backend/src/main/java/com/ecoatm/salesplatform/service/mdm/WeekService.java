package com.ecoatm.salesplatform.service.mdm;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeekService {

    private final WeekRepository weekRepository;

    public WeekService(WeekRepository weekRepository) {
        this.weekRepository = weekRepository;
    }

    public Optional<Week> findCurrentWeek() {
        return weekRepository.findFutureWeeks().stream().findFirst();
    }

    public List<Week> findAllDescending() {
        return weekRepository.findAllByOrderByWeekStartDateTimeDesc();
    }
}
