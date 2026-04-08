# Microflow Detailed Specification: SUB_AllBids_ExportExcel_PerBuyerCode

### 📥 Inputs (Parameters)
- **$AllBidDownloadList** (Type: AuctionUI.AllBidDownload)
- **$AllBidsTempList** (Type: AuctionUI.AllBidsZipTempList)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$MxTemplate** (Type: XLSReport.MxTemplate)
- **$Auction** (Type: AuctionUI.Auction)
- **$AuctionSubFolder** (Type: Variable)
- **$UserSubmitedBidSubmitLog** (Type: AuctionUI.BidSubmitLog)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$MxTemplate!=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc**)
      - Set **DeleteAfterDownload** = `true`**
      2. 🔄 **LOOP:** For each **$IteratorAllBidsData** in **$AllBidDownloadList**
         │ 1. **Update **$IteratorAllBidsData**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
         └─ **End Loop**
      3. **Commit/Save **$AllBidDownloadList** to Database**
      4. **Create **AuctionUI.AllBidsZipTempList** (Result: **$NewAllBidsZipTempList**)**
      5. **JavaCallAction**
      6. **Call Microflow **AuctionUI.Sub_GetAuctionEndDate** (Result: **$LastSchedulingAuction**)**
      7. **Update **$NewAllBidsZipTempList**
      - Set **Name** = `$BuyerCode/Code+'_'+formatDateTime($LastSchedulingAuction/End_DateTime, 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
      8. **Add **$$NewAllBidsZipTempList** to/from list **$AllBidsTempList****
      9. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
      10. **Create Variable **$RetryCount** = `$BuyerCodeSubmitConfig/SPRetryCount`**
      11. **Create Variable **$RetryAtempt** = `0`**
      12. **Call Microflow **AuctionUI.ACT_LogSendToSharepoint** (Result: **$BidSubmitLog_Upload**)**
      13. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess**)**
      14. 🔀 **DECISION:** `$UploadSuccess`
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.ACT_UpdateBidSubmitLog** (Result: **$BidSubmitLog_2**)**
            2. **Delete**
            3. **Commit/Save **$AllBidDownloadList** to Database**
            4. **LogMessage**
            5. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **LogMessage**
            2. **Call Microflow **AuctionUI.ACT_UpdateBidSubmitLog** (Result: **$BidSubmitLog_2**)**
            3. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.