# Export Mapping: EXP_AuctionData

**JSON Structure:** `AuctionUI.JSON_AuctionData`

## Mapping Structure

- **Root** (Object)
  - **Week** (Object) → `EcoATM_MDM.Week`
    - **WeekNumber** (Value)
      - Attribute: `EcoATM_MDM.Week.WeekNumber`
    - **Year** (Value)
      - Attribute: `EcoATM_MDM.Week.Year`
    - **Auction** (Object) → `AuctionUI.Auction`
      - **AuctionTitle** (Value)
        - Attribute: `AuctionUI.Auction.AuctionTitle`
      - **AuctionStatus** (Value)
        - Attribute: `AuctionUI.Auction.AuctionStatus`
      - **Created_By** (Value)
        - Attribute: `AuctionUI.Auction.CreatedBy`
      - **Updated_By** (Value)
        - Attribute: `AuctionUI.Auction.UpdatedBy`
      - **SchedulingAuction** (Array)
        - **SchedulingAuctionItem** (Object) → `AuctionUI.SchedulingAuction`
          - **AuctionWeekYear** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.Auction_Week_Year`
          - **Round** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.Round`
          - **StartDateTime** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.Start_DateTime`
          - **EndDateTime** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.End_DateTime`
          - **RoundStatus** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.RoundStatus`
          - **HasRound** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.HasRound`
          - **Created_By** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.CreatedBy`
          - **Updated_By** (Value)
            - Attribute: `AuctionUI.SchedulingAuction.UpdatedBy`
