# Nanoflow: NAN_BidDataAdmin_OnScheduleAuctionChange

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.SalesRep

## 📥 Inputs

- **$BidDataQueryHelper** (AuctionUI.BidDataQueryHelper)

## ⚙️ Execution Flow

1. **Update **$BidDataQueryHelper** (and Save to DB)
      - Set **BidDataQueryHelper_BuyerCode** = `empty`**
2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
