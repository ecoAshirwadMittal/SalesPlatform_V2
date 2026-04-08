# Microflow Detailed Specification: SUB_Purge_Auction

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. **JavaCallAction**
      3. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      4. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      5. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      6. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      7. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      8. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      9. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      10. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      11. **Call Microflow **Custom_Logging.SUB_Log_Info****
      12. **DB Retrieve **AuctionUI.BidRound** Filter: `[IsDeprecated]` (Result: **$BidRoundList**)**
      13. **Delete**
      14. **Call Microflow **Custom_Logging.SUB_Log_Info****
      15. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.Auction_Week=$Week]` (Result: **$AuctionList**)**
      16. **Delete**
      17. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      18. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.