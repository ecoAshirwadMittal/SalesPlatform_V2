package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Custom slice of {@link ReserveBidRepository}. Hosts the dynamic-WHERE
 * search() that the {@link org.springframework.data.jpa.repository.Query @Query}
 * annotation cannot express. Spring Data wires this in automatically via the
 * {@code RepositoryImpl} naming convention (companion class
 * {@code ReserveBidRepositoryImpl}).
 */
public interface ReserveBidRepositoryCustom {

    /**
     * Page reserve bids matching every spec (AND semantics). Each spec's
     * column + op are pre-validated by {@link FilterSpec}, so this method
     * only needs to translate ops to SQL fragments.
     *
     * @param filters AND-composed predicates; an empty list returns all rows
     * @param pageable {@code Pageable.getSort()} is honoured against SQL column
     *                 names matching {@link com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterColumn#sqlName()}.
     */
    Page<ReserveBid> searchDynamic(List<FilterSpec> filters, Pageable pageable);
}
