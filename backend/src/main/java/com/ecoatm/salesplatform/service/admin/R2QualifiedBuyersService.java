package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.admin.R2QualifiedBuyerRow;
import com.ecoatm.salesplatform.dto.admin.R2QualifiedBuyersResponse;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Read-only admin service for the R2 Qualified Buyer Codes grid (gap H10).
 *
 * <p>Resolves the R2 {@code SchedulingAuction} for the supplied auction id,
 * then joins {@code qualified_buyer_codes} → {@code buyer_codes} →
 * {@code buyer_code_buyers} → {@code buyers} so each row carries the
 * human-friendly buyer code + company name. The page exists so SalesOps
 * can inspect WHO the R2 buyer-assignment service decided to qualify
 * (and which were special-treatment vs regular) — the criteria editor
 * already exists, the result view did not.
 */
@Service
public class R2QualifiedBuyersService {

    private final AuctionRepository auctionRepo;
    private final SchedulingAuctionRepository saRepo;
    private final EntityManager em;

    public R2QualifiedBuyersService(AuctionRepository auctionRepo,
                                    SchedulingAuctionRepository saRepo,
                                    EntityManager em) {
        this.auctionRepo = auctionRepo;
        this.saRepo = saRepo;
        this.em = em;
    }

    private static final String GRID_SQL = """
            SELECT bc.id            AS buyer_code_id,
                   bc.code          AS code,
                   bc.buyer_code_type,
                   COALESCE(
                     (SELECT b.company_name
                        FROM buyer_mgmt.buyer_code_buyers bcb
                        JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                       WHERE bcb.buyer_code_id = bc.id
                       ORDER BY bcb.buyer_id
                       LIMIT 1),
                     ''
                   )                AS company_name,
                   q.qualification_type,
                   q.included,
                   q.is_special_treatment
              FROM buyer_mgmt.qualified_buyer_codes q
              JOIN buyer_mgmt.buyer_codes bc ON bc.id = q.buyer_code_id
             WHERE q.scheduling_auction_id = :saId
             ORDER BY q.qualification_type DESC,    -- Qualified first, then Not_Qualified
                      q.is_special_treatment DESC,  -- Special-treatment first within Qualified
                      bc.code ASC
            """;

    @Transactional(readOnly = true, timeout = 10)
    public R2QualifiedBuyersResponse findByAuction(Long auctionId) {
        if (auctionId == null) {
            throw new IllegalArgumentException("auctionId is required");
        }

        Auction auction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("auction", auctionId));

        SchedulingAuction r2Sa = saRepo.findByAuctionIdAndRound(auctionId, 2)
                .orElseThrow(() -> new EntityNotFoundException(
                        "scheduling_auction.round=2 for auction", auctionId));

        @SuppressWarnings("unchecked")
        List<Object[]> rawRows = em.createNativeQuery(GRID_SQL)
                .setParameter("saId", r2Sa.getId())
                .getResultList();

        List<R2QualifiedBuyerRow> rows = rawRows.stream()
                .map(r -> new R2QualifiedBuyerRow(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4],
                        (Boolean) r[5],
                        (Boolean) r[6]))
                .toList();

        int qualifiedCount = (int) rows.stream()
                .filter(r -> "Qualified".equals(r.qualificationType()) && !r.isSpecialTreatment())
                .count();
        int specialCount = (int) rows.stream()
                .filter(R2QualifiedBuyerRow::isSpecialTreatment)
                .count();
        int notQualifiedCount = (int) rows.stream()
                .filter(r -> "Not_Qualified".equals(r.qualificationType()))
                .count();

        return new R2QualifiedBuyersResponse(
                auctionId,
                auction.getAuctionTitle(),
                r2Sa.getId(),
                r2Sa.getR2InitStatus() == null ? null : r2Sa.getR2InitStatus().name(),
                rows.size(),
                qualifiedCount,
                specialCount,
                notQualifiedCount,
                rows);
    }

    // Mute unused-imports warnings until any audit-timestamp field is needed.
    @SuppressWarnings("unused")
    private static Instant fromSqlTimestamp(Object o) {
        return o instanceof Timestamp ts ? ts.toInstant() : null;
    }
}
