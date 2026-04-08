# Microflow Analysis: SUB_SetFinalValuesToAcceptedItems

### Requirements (Inputs):
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Accept }, and call the result **$OfferItemList_Accepted****
4. **Decision:** "list not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
