# Microflow Analysis: SUB_BuyerOffer_CreateOfferAndOrder

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)
- **$BuyerOfferItemList** (A record of type: EcoATM_PWS.BuyerOfferItem)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Create Object
      - Store the result in a new variable called **$NewOrder****
4. **Create Object
      - Store the result in a new variable called **$NewOffer****
5. **Create List
      - Store the result in a new variable called **$OfferItemList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Aggregate List
      - Store the result in a new variable called **$SumTotalQuantity****
8. **Aggregate List
      - Store the result in a new variable called **$SumTotalPrice****
9. **Search the Database for **Administration.Account** using filter: { [id = $currentUser] } (Call this list **$Account**)**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferTotalQuantity] to: "$SumTotalQuantity
"
      - Change [EcoATM_PWS.Offer.OfferTotalPrice] to: "$SumTotalPrice
"
      - Change [EcoATM_PWS.Offer.OfferSubmissionDate] to: "[%CurrentDateTime%]
"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.OfferSubmittedBy_Account] to: "$Account"
      - **Save:** This change will be saved to the database immediately.**
11. **Permanently save **$undefined** to the database.**
12. **Run another process: "Custom_Logging.SUB_Log_Info"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
