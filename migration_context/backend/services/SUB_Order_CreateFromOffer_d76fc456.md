# Microflow Analysis: SUB_Order_CreateFromOffer

### Requirements (Inputs):
- **$FinalOffer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer=$FinalOffer]
[SalesOfferItemStatus='Accept' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept') or SalesOfferItemStatus = 'Finalize'] } (Call this list **$AcceptedFizLIZEOfferItemList**)**
5. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
7. **Run another process: "EcoATM_PWS.SUB_Offer_CreateOrder"
      - Store the result in a new variable called **$Order****
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
