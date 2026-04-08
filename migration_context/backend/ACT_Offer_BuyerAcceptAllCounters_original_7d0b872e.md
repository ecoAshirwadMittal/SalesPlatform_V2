# Microflow Analysis: ACT_Offer_BuyerAcceptAllCounters_original

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer = $Offer]
[SalesOfferItemStatus='Counter' or SalesOfferItemStatus='Accept'] } (Call this list **$OfferItemList**)**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferQuantity****
8. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferPrice****
9. **Aggregate List
      - Store the result in a new variable called **$CountSKU****
10. **Permanently save **$undefined** to the database.**
11. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Ordered"
      - Change [EcoATM_PWS.Offer.FinalOfferSubmittedOn] to: "[%CurrentDateTime%]
"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalSKU] to: "$CountSKU
"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalQty] to: "$SumFinalOfferQuantity
"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalPrice] to: "$SumFinalOfferPrice
"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
12. **Close Form**
13. **Show Page**
14. **Create Object
      - Store the result in a new variable called **$AcceptedUserMessage****
15. **Show Page**
16. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
