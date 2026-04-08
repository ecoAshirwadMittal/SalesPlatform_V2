# Microflow Analysis: SUB_BuyerOffer_RemoveRecords

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Retrieve
      - Store the result in a new variable called **$BuyerOfferItemList****
4. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Delete**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOffer.OfferTotal] to: "0
"
      - Change [EcoATM_PWS.BuyerOffer.OfferStatus] to: "empty
"
      - Change [EcoATM_PWS.BuyerOffer.OfferSKUs] to: "0"
      - Change [EcoATM_PWS.BuyerOffer.OfferQuantity] to: "0"
      - **Save:** This change will be saved to the database immediately.**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
