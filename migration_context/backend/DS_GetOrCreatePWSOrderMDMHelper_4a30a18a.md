# Microflow Analysis: DS_GetOrCreatePWSOrderMDMHelper

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "AuctionUI.ACT_GET_CurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
5. **Retrieve
      - Store the result in a new variable called **$PWSMDMHelper****
6. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$CaseLotsFeatureFlag****
7. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$AYYY_FeatureFlag****
8. **Decision:** "PWSMDMHelper Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.PWSUserPersonalization.EnableCaseLots] to: "$CaseLotsFeatureFlag
"
      - Change [EcoATM_PWS.PWSUserPersonalization.EnableAYYY] to: "$AYYY_FeatureFlag
"
      - **Save:** This change will be saved to the database immediately.**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
