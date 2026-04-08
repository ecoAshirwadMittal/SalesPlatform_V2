# Microflow Detailed Specification: ACT_RemoveRound2BuyerCode

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
3. **List Operation: **Find** on **$undefined** where `2` (Result: **$SchedulingAuction**)**
4. **Retrieve related **SchedulingAuction_QualifiedBuyers** via Association from **$SchedulingAuction** (Result: **$BuyerCodeList**)**
5. **Remove **$$BuyerCode** to/from list **$BuyerCodeList****
6. **Update **$SchedulingAuction**
      - Set **SchedulingAuction_QualifiedBuyers** = `$BuyerCodeList`**
7. **Commit/Save **$SchedulingAuction** to Database**
8. **Update **$Auction** (and Save to DB)**
9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.