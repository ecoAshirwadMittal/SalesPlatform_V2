# Microflow Detailed Specification: ACT_TransferBuyerCodeBidsToSharepoint

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidSubmitLog** (Type: AuctionUI.BidSubmitLog)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.AuctionsFeature**  (Result: **$AuctionsFeature**)**
2. **Retrieve related **QualifiedBuyerCodes_BidRound** via Association from **$BidRound** (Result: **$QualifiedBuyerCodes**)**
3. 🔀 **DECISION:** `$QualifiedBuyerCodes != empty`
   ➔ **If [true]:**
      1. **Update **$QualifiedBuyerCodes**
      - Set **Submitted** = `true`
      - Set **SubmittedDateTime** = `[%CurrentDateTime%]`
      - Set **QualifiedBuyerCodes_SubmittedBy** = `$currentUser`**
      2. 🔀 **DECISION:** `$AuctionsFeature/SendFilesToSharepointOnSubmit = true`
         ➔ **If [true]:**
            1. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
            2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
            3. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc**)
      - Set **DeleteAfterDownload** = `true`**
            4. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
            5. **CreateList**
            6. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
            7. **CreateList**
            8. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week/AuctionUI.Auction=$Auction] and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode= $BuyerCode]` (Result: **$BidDataList**)**
            9. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round = 1`
                     ➔ **If [true]:**
                        1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week and DWTotalQuantity > 0 ]` (Result: **$AgregatedInventoryList_DataWipe**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ BidAmount > 0 and AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted = true and AuctionUI.BidRound_BuyerCode = $BuyerCode]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction = $Auction] ]` (Result: **$AgregatedInventoryList_DataWipe_bidsonly**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round = 1`
                     ➔ **If [true]:**
                        1. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ BidAmount > 0 and AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted = true and AuctionUI.BidRound_BuyerCode = $BuyerCode]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction = $Auction] ]` (Result: **$AgregatedInventoryList_bidsonly**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$AuctionsFeature/SendFilesToSharepointOnSubmit = true`
         ➔ **If [true]:**
            1. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
            2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
            3. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc**)
      - Set **DeleteAfterDownload** = `true`**
            4. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
            5. **CreateList**
            6. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
            7. **CreateList**
            8. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week/AuctionUI.Auction=$Auction] and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode= $BuyerCode]` (Result: **$BidDataList**)**
            9. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round = 1`
                     ➔ **If [true]:**
                        1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week and DWTotalQuantity > 0 ]` (Result: **$AgregatedInventoryList_DataWipe**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ BidAmount > 0 and AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted = true and AuctionUI.BidRound_BuyerCode = $BuyerCode]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction = $Auction] ]` (Result: **$AgregatedInventoryList_DataWipe_bidsonly**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round = 1`
                     ➔ **If [true]:**
                        1. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ BidAmount > 0 and AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted = true and AuctionUI.BidRound_BuyerCode = $BuyerCode]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction = $Auction] ]` (Result: **$AgregatedInventoryList_bidsonly**)**
                        2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
                        3. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$FileUploaded**)**
                        4. **Delete**
                        5. **Update **$BidRound** (and Save to DB)
      - Set **UploadedToSharepoint** = `$FileUploaded`
      - Set **UploadToSharepointDateTime** = `if($FileUploaded) then [%CurrentDateTime%] else $BidRound/UploadToSharepointDateTime`**
                        6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        7. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.