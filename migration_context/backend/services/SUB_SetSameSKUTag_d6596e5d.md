# Microflow Analysis: SUB_SetSameSKUTag

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Run another process: "Custom_Logging.SUB_Log_Debug"**
3. **Create List
      - Store the result in a new variable called **$OfferList****
4. **Retrieve
      - Store the result in a new variable called **$OfferItemList_Current****
5. **Create List
      - Store the result in a new variable called **$OfferItemList_Complete****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
