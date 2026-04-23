package com.ecoatm.salesplatform.repository.admin;

import com.ecoatm.salesplatform.model.admin.BuyerUserGuide;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerUserGuideRepository extends JpaRepository<BuyerUserGuide, Long> {

    /** The currently active (and not soft-deleted) guide, if one exists. */
    Optional<BuyerUserGuide> findByIsActiveTrue();

    /**
     * The last {@code n} non-deleted entries ordered newest first.
     * Used for the admin history table.
     */
    @Query("SELECT g FROM BuyerUserGuide g WHERE g.isDeleted = false ORDER BY g.uploadedAt DESC")
    List<BuyerUserGuide> findRecentUploads(Pageable pageable);

    /** Clear the active flag on all rows — called before activating a new upload. */
    @Modifying
    @Query("UPDATE BuyerUserGuide g SET g.isActive = false WHERE g.isActive = true")
    void deactivateAll();

    /**
     * Find a non-deleted entry by id — used for download and delete operations.
     */
    @Query("SELECT g FROM BuyerUserGuide g WHERE g.id = :id AND g.isDeleted = false")
    Optional<BuyerUserGuide> findActiveById(@Param("id") long id);
}
