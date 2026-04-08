# Microflow Detailed Specification: SUB_GetBidImportSheetName

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SchedulingAuction/Round=1`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `'BidDataExport'`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BidRanking**  (Result: **$BidRanking**)**
      2. 🔀 **DECISION:** `$BidRanking/DisplayRank`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `'BidDataExport'`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `'BidDataRankRound2Export'`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `empty`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `'BidDataRankRound3Export'`

**Final Result:** This process concludes by returning a [String] value.