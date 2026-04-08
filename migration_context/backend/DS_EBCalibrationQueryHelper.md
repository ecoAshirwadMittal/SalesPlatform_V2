# Microflow Detailed Specification: DS_EBCalibrationQueryHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EBCalibrationQueryHelper_Session** via Association from **$currentSession** (Result: **$BidDataQueryHelperList**)**
2. 🔀 **DECISION:** `$BidDataQueryHelperList !=empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$EBCalibrationQueryHelper**)**
      2. **Delete**
      3. **Create **EcoATM_Reports.EBCalibrationQueryHelper** (Result: **$NewBidDataQueryHelper**)
      - Set **EBCalibrationQueryHelper_Session** = `$currentSession`
      - Set **DisplayReport** = `false`**
      4. 🏁 **END:** Return `$NewBidDataQueryHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_Reports.EBCalibrationQueryHelper** (Result: **$NewBidDataQueryHelper**)
      - Set **EBCalibrationQueryHelper_Session** = `$currentSession`
      - Set **DisplayReport** = `false`**
      2. 🏁 **END:** Return `$NewBidDataQueryHelper`

**Final Result:** This process concludes by returning a [Object] value.