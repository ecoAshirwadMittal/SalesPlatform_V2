# Microflow Detailed Specification: SUB_GenerateQualifiedBuyerCodes

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BuyerCodeList_Qualified** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'GenerateQualifiedBuyers'`**
2. **Create Variable **$Description** = `'Generate Qualified Buyers: Auction-' + $Auction/AuctionTitle + ' , Round-' + $SchedulingAuction/Round`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **AuctionUI.SUB_ClearQualifiedBuyerList****
5. **Call Microflow **EcoATM_BuyerManagement.SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe** (Result: **$SpecialBuyerCodeList**)**
6. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_FinalQualified**)**
7. **Call Microflow **AuctionUI.SUB_CreateQualifiedBuyersEntity****
8. **Call Microflow **AuctionUI.SUB_CreateQualifiedBuyersEntity****
9. **List Operation: **Union** on **$undefined** (Result: **$BuyerCodeList_AllQualifedAndSPT**)**
10. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale' ) and ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' ) ]` (Result: **$BuyerCodeList_All_Active**)**
11. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_NotQualified**)**
12. **Call Microflow **AuctionUI.SUB_CreateQualifiedBuyersEntity****
13. **Update **$SchedulingAuction** (and Save to DB)
      - Set **SchedulingAuction_QualifiedBuyers** = `$BuyerCodeList_AllQualifedAndSPT`**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return `$BuyerCodeList_AllQualifedAndSPT`

**Final Result:** This process concludes by returning a [List] value.