# Nanoflow: NAN_BidDataAdmin_OnAuctionChange

**Allowed Roles:** AuctionUI.SalesOps, AuctionUI.SalesRep

## 📥 Inputs

- **$BidDataQueryHelper** (AuctionUI.BidDataQueryHelper)

## ⚙️ Execution Flow

1. **Update **$BidDataQueryHelper** (and Save to DB)
      - Set **BidDataQueryHelper_SchedulingAuction** = `empty`
      - Set **BidDataQueryHelper_BuyerCode** = `empty`**
2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
