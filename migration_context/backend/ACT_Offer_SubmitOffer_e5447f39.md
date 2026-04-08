# Microflow Analysis: ACT_Offer_SubmitOffer

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Search the Database for **EcoATM_PWS.BuyerOfferItem** using filter: { [EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer]
[TotalPrice>0]
 } (Call this list **$BuyerOfferItemWithPriceList**)**
6. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_CreateOffer"
      - Store the result in a new variable called **$Offer**** ⚠️ *(This step has a safety catch if it fails)*
8. **Decision:** "Offer Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
9. **Run another process: "EcoATM_PWS.SUB_RetrieveOrderStatus"
      - Store the result in a new variable called **$OrderStatus****
10. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review
"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.Offer_OrderStatus] to: "$OrderStatus
"
      - **Save:** This change will be saved to the database immediately.**
11. **Run another process: "EcoATM_PWS.SUB_UpdateOfferDrawerStatus"**
12. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_RemoveRecords"**
13. **Run another process: "EcoATM_PWS.SUB_SendPWSOfferConfirmationEmail"**
14. **Close Form**
15. **Show Page**
16. **Create Object
      - Store the result in a new variable called **$SuccessUserMessage****
17. **Show Page**
18. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
