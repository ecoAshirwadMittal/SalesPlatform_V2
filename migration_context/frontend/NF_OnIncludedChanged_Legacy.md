# Nanoflow: NF_OnIncludedChanged_Legacy

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

## 📥 Inputs

- **$QualifiedBuyerCodes** (EcoATM_BuyerManagement.QualifiedBuyerCodes)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `($QualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 3 and $QualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction/AuctionUI.SchedulingAuction/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Scheduled) or $QualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 2 or $QualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ReturnValueName**)**
      2. **Update **$QualifiedBuyerCodes** (and Save to DB)
      - Set **Included** = `$QualifiedBuyerCodes/Included`
      - Set **Qualificationtype** = `EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Manual`**
      3. 🔀 **DECISION:** `$QualifiedBuyerCodes/Included = true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$QualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction/AuctionUI.SchedulingAuction/Round != 3`
               ➔ **If [true]:**
                  1. **Retrieve related **QualifiedBuyerCodes_BidRound** via Association from **$QualifiedBuyerCodes** (Result: **$BidRound**)**
                  2. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList_existing**)**
                  3. **Aggregate **Count** on **$BidDataList_existing** (Result: **$Count**)**
                  4. 🔀 **DECISION:** `$Count = 0`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_BuyerManagement.SUB_CreateBidDataForAllAE** (Result: **$BidDataList**)**
                        2. **Call Microflow **AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport** (Result: **$RoundThreeBuyersDataReportList**)**
                        3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
                        4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport** (Result: **$RoundThreeBuyersDataReportList**)**
                        2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$QualifiedBuyerCodes** (Result: **$SchedulingAuction_Round3**)**
                  2. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction_Round3** (Result: **$Auction**)**
                  3. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
                  4. **List Operation: **Filter** on **$SchedulingAuctionList** where `2` (Result: **$SchedulingAuctionList_FilteredRound2**)**
                  5. **List Operation: **Head** on **$SchedulingAuctionList_FilteredRound2** (Result: **$SchedulingAuction_Round2**)**
                  6. **Call Microflow **EcoATM_BuyerManagement.SUB_CreateBidDataForAllAE** (Result: **$BidDataList_1**)**
                  7. **Call Microflow **AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport** (Result: **$RoundThreeBuyersDataReportList**)**
                  8. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
                  9. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport** (Result: **$RoundThreeBuyersDataReportList**)**
            2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Round 3 cannot be modified if it is started or closed.`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
