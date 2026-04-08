# Microflow Detailed Specification: ACT_OpenRound2QualifiedInventory

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$Week**)**
2. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
3. **Maps to Page: **AuctionUI.PG_Round2QualifyingInventory****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.