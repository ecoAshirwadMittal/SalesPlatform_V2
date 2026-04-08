# Microflow Detailed Specification: ACT_Delete_AuctionFromAdmin

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
2. **Delete**
3. **ExecuteDatabaseQuery**
4. **ExecuteDatabaseQuery**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.