# Microflow Detailed Specification: SUB_GetBidDataExportTemplate

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SchedulingAuction/Round=1`
   ➔ **If [true]:**
      1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataExport']` (Result: **$MxTemplate**)**
      2. 🏁 **END:** Return `$MxTemplate`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BidRanking**  (Result: **$BidRanking**)**
      2. 🔀 **DECISION:** `$BidRanking/DisplayRank`
         ➔ **If [false]:**
            1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataExport']` (Result: **$MxTemplate**)**
            2. 🏁 **END:** Return `$MxTemplate`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
               ➔ **If [true]:**
                  1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[ ( Name = 'BidDataRankRound2Export' ) ]` (Result: **$MxTemplate_Round2**)**
                  2. 🏁 **END:** Return `$MxTemplate_Round2`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `empty`
                     ➔ **If [true]:**
                        1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[ ( Name = 'BidDataRankRound3Export' ) ]` (Result: **$MxTemplate_Round3**)**
                        2. 🏁 **END:** Return `$MxTemplate_Round3`

**Final Result:** This process concludes by returning a [Object] value.