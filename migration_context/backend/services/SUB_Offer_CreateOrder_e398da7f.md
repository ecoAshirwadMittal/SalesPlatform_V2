# Microflow Analysis: SUB_Offer_CreateOrder

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
3. **Run another process: "Custom_Logging.SUB_Log_Info"**
4. **Create Object
      - Store the result in a new variable called **$NewOrder****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
