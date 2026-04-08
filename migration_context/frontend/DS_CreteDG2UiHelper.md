# Nanoflow: DS_CreteDG2UiHelper

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

## 📥 Inputs

- **$SchedulingAuction** (AuctionUI.SchedulingAuction)

## ⚙️ Execution Flow

1. **DB Retrieve **AuctionUI.BidRanking**  (Result: **$BidRanking**)**
2. **Create **EcoATM_BuyerManagement.DG2_UiHelper** (Result: **$NewDG2_UiHelper**)
      - Set **IsBidDataLoaded** = `false`
      - Set **DisplayBidRankColumn** = `if $SchedulingAuction/Round=1 then false else if (($SchedulingAuction/Round=3 or $SchedulingAuction/Round=2) and $BidRanking/DisplayRank=true) then true else if (($SchedulingAuction/Round=3 or $SchedulingAuction/Round=2) and $BidRanking/DisplayRank=false) then false else false`**
3. 🏁 **END:** Return `$NewDG2_UiHelper`

## 🏁 Returns
`Object`
