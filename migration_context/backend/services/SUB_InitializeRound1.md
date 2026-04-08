# Microflow Detailed Specification: SUB_InitializeRound1

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' ) and ( BuyerCodeType = 'Wholesale' or BuyerCodeType = 'Data_Wipe' ) ]` (Result: **$BuyerCodeList**)**
2. **Call Microflow **AuctionUI.ACT_UpdateRound1TargetPrice_MinBid****
3. **Call Microflow **AuctionUI.SUB_CreateQualifiedBuyersEntity****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.