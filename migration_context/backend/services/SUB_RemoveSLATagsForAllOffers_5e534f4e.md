# Microflow Analysis: SUB_RemoveSLATagsForAllOffers

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [(OfferStatus='Sales_Review')
or
(OfferStatus = 'Buyer_Acceptance' )]
 } (Call this list **$OfferList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Show Message**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
