# Microflow Analysis: VAL_Offer_hasRespectedAvailableQuantities

### Requirements (Inputs):
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
