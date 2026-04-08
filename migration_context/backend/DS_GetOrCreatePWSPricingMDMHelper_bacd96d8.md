# Microflow Analysis: DS_GetOrCreatePWSPricingMDMHelper

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "AuctionUI.ACT_GET_CurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
5. **Retrieve
      - Store the result in a new variable called **$PWSMDMHelper****
6. **Decision:** "PWSMDMHelper Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
