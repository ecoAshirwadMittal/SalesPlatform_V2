# Microflow Detailed Specification: Act_GetOrCreateBuyerCodeSubmitConfig

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
2. **DB Retrieve **EcoATM_BuyerManagement.AuctionsFeature**  (Result: **$BuyerCodeSubmitConfig**)**
3. 🔀 **DECISION:** `$BuyerCodeSubmitConfig = empty`
   ➔ **If [true]:**
      1. **Create **EcoATM_BuyerManagement.AuctionsFeature** (Result: **$NewBuyerCodeSubmitConfig**)
      - Set **SendAuctionDataToSnowflake** = `true`
      - Set **SendBuyerToSnowflake** = `true`
      - Set **SendBidDataToSnowflake** = `true`
      - Set **AuctionRound2MinutesOffset** = `0`
      - Set **AuctionRound3MinutesOffset** = `0`
      - Set **SPRetryCount** = `3`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$NewBuyerCodeSubmitConfig`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `$BuyerCodeSubmitConfig`

**Final Result:** This process concludes by returning a [Object] value.