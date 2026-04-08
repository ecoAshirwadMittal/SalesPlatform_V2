# Microflow Analysis: SUB_SetAndResetTags

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus = 'Buyer_Acceptance' or OfferStatus = 'Sales_Review']
 } (Call this list **$OfferList_SaleReviewOrBuyerAcceptance**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Run another process: "EcoATM_PWS.SUB_RemoveSameSKUTag"**
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
