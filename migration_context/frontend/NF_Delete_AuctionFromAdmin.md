# Nanoflow: NF_Delete_AuctionFromAdmin

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps

## 📥 Inputs

- **$Auction** (AuctionUI.Auction)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Variable**)**
2. **Call Microflow **AuctionUI.SUB_Auction_DeleteByAdmin****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
