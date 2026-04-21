package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.CreateAuctionRequest;
import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.exception.AuctionAlreadyExistsException;
import com.ecoatm.salesplatform.exception.DuplicateAuctionTitleException;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Porting target: Mendix {@code ACT_Create_Auction} + {@code VAL_Create_Auction}.
 *
 * <p>Invariants mirrored from legacy (per 2026-04-20 ADR amendment):
 * <ul>
 *   <li>One auction per week (gated by {@link AuctionRepository#existsByWeekId}).</li>
 *   <li>Title is case-insensitively unique across {@code auctions.auctions}.</li>
 *   <li>Create persists <strong>only</strong> the {@code Auction} row. The three
 *       {@code SchedulingAuction} rounds are persisted later by the Schedule
 *       endpoint ({@code PUT /admin/auctions/{id}/schedule}, Phase C). An
 *       auction in status {@link AuctionStatus#Unscheduled} legitimately has
 *       zero rounds until the admin confirms the schedule.</li>
 *   <li>The newly-created auction starts in {@link AuctionStatus#Unscheduled};
 *       the Save Schedule write flips it to {@code Scheduled}.</li>
 * </ul>
 */
@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final WeekRepository weekRepository;

    public AuctionService(AuctionRepository auctionRepository,
                          WeekRepository weekRepository) {
        this.auctionRepository = auctionRepository;
        this.weekRepository = weekRepository;
    }

    @Transactional
    public CreateAuctionResponse createAuction(CreateAuctionRequest req) {
        if (req == null || req.weekId() == null) {
            throw new IllegalArgumentException("weekId is required");
        }

        Week week = weekRepository.findById(req.weekId())
                .orElseThrow(() -> new EntityNotFoundException("Week", req.weekId()));

        if (auctionRepository.existsByWeekId(week.getId())) {
            throw new AuctionAlreadyExistsException(week.getId());
        }

        String title = buildAuctionTitle(week.getWeekDisplay(), req.customSuffix());
        if (auctionRepository.existsByAuctionTitleIgnoreCase(title)) {
            throw new DuplicateAuctionTitleException(title);
        }

        Instant now = Instant.now();
        String actor = currentUsername();

        Auction auction = new Auction();
        auction.setAuctionTitle(title);
        auction.setAuctionStatus(AuctionStatus.Unscheduled);
        auction.setWeekId(week.getId());
        auction.setCreatedBy(actor);
        auction.setUpdatedBy(actor);
        auction.setCreatedDate(now);
        auction.setChangedDate(now);
        Auction saved = auctionRepository.save(auction);

        return new CreateAuctionResponse(
                saved.getId(),
                saved.getAuctionTitle(),
                saved.getAuctionStatus().name(),
                week.getId(),
                week.getWeekDisplay());
    }

    /**
     * Mirrors Mendix title composition: "Auction " + weekDisplay, optionally
     * suffixed by a trimmed custom label separated by a single space.
     */
    static String buildAuctionTitle(String weekDisplay, String customSuffix) {
        String base = "Auction " + (weekDisplay == null ? "" : weekDisplay.trim());
        if (customSuffix == null) {
            return base;
        }
        String trimmed = customSuffix.trim();
        if (trimmed.isEmpty()) {
            return base;
        }
        return base + " " + trimmed;
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
