package com.ecoatm.salesplatform.service.auctions.biddata;

import java.util.List;

public sealed interface BidderDashboardLandingResult
        permits BidderDashboardLandingResult.Grid,
                BidderDashboardLandingResult.Download,
                BidderDashboardLandingResult.Ended,
                BidderDashboardLandingResult.Error,
                BidderDashboardLandingResult.AllDone {

    record Grid(long bidRoundId, long schedulingAuctionId, int round)
            implements BidderDashboardLandingResult {}

    record Download(String reason) implements BidderDashboardLandingResult {}

    /**
     * Most-recent auction has ended — no round is {@code Started} but a
     * scheduling auction exists. Mirrors legacy
     * {@code ACT_OpenBidderDashboard} routing to
     * {@code BidDownloadOnBuyerCodeSelect} (the "Bidding has ended." page).
     * Carries the auction heading and the rounds this buyer code can
     * download (one download button per round it participated in).
     */
    record Ended(String auctionTitle, List<Integer> downloadRounds)
            implements BidderDashboardLandingResult {}

    record Error(String reason) implements BidderDashboardLandingResult {}

    record AllDone() implements BidderDashboardLandingResult {}
}
