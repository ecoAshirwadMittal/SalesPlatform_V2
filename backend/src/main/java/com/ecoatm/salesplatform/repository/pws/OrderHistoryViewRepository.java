package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.OrderHistoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderHistoryViewRepository
        extends JpaRepository<OrderHistoryView, Long>,
                JpaSpecificationExecutor<OrderHistoryView> {
}
