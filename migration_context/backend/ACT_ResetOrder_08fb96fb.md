# Microflow Analysis: ACT_ResetOrder

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOffer.OfferTotal] to: "0
"
      - Change [EcoATM_PWS.BuyerOffer.OfferSKUs] to: "0"
      - Change [EcoATM_PWS.BuyerOffer.OfferQuantity] to: "0"
      - **Save:** This change will be saved to the database immediately.**
5. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Update the **$undefined** (Object):**
9. **Close Form**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
