package com.ecoatm.salesplatform.service.auctions.biddata;

public sealed interface BidderDashboardLandingResult
        permits BidderDashboardLandingResult.Grid,
                BidderDashboardLandingResult.Download,
                BidderDashboardLandingResult.Error,
                BidderDashboardLandingResult.AllDone {

    record Grid(long bidRoundId, long schedulingAuctionId, int round)
            implements BidderDashboardLandingResult {}

    record Download(String reason) implements BidderDashboardLandingResult {}

    record Error(String reason) implements BidderDashboardLandingResult {}

    record AllDone() implements BidderDashboardLandingResult {}
}
