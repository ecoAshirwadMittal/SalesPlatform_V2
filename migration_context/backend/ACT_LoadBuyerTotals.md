# Microflow Detailed Specification: ACT_LoadBuyerTotals

### 📥 Inputs (Parameters)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' ) ]` (Result: **$ActiveBuyerCodeList**)**
2. **AggregateList**
3. **List Operation: **Filter** on **$undefined** where `AuctionUI.enum_BuyerCodeType.Data_Wipe` (Result: **$BuyerCodeList_DW**)**
4. **AggregateList**
5. **Update **$SchedulingAuction_Helper**
      - Set **BuyersTotal** = `$AllBuyerCodeCount`
      - Set **BuyersDWOnly** = `$DWBuyerCount`**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.