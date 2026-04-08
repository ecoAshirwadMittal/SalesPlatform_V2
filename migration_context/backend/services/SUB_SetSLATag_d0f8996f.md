# Microflow Analysis: SUB_SetSLATag

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Run another process: "EcoATM_PWS.SUB_CalculateSLADate"
      - Store the result in a new variable called **$ResultDate****
3. **Search the Database for **EcoATM_PWS.Offer** using filter: { [(OfferStatus='Sales_Review')
or
(OfferStatus = 'Buyer_Acceptance' )]
 } (Call this list **$OfferList**)**
4. **Take the list **$OfferList**, perform a [FilterByExpression] where: { trimToDays($currentObject/UpdateDate) <= trimToDays($ResultDate) }, and call the result **$OfferList_filtered****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
