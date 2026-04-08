package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
