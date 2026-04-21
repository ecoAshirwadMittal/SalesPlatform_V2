package com.ecoatm.salesplatform.infra.scheduledjob;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledJobRunRepository extends JpaRepository<ScheduledJobRun, Long> {
}
