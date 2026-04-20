package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.CreateAuctionRequest;
import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.service.auctions.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/auctions")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<CreateAuctionResponse> create(@RequestBody CreateAuctionRequest req) {
        CreateAuctionResponse response = auctionService.createAuction(req);
        return ResponseEntity.created(URI.create("/api/v1/admin/auctions/" + response.id()))
                .body(response);
    }
}
