# Microflow Detailed Specification: SUB_Round1BuyerCodes

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer[EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser/Name=$currentUser/Name]]` (Result: **$BuyerCodeList_UserAssigned**)**
2. 🏁 **END:** Return `$BuyerCodeList_UserAssigned`

**Final Result:** This process concludes by returning a [Object] value.