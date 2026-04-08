# Microflow Analysis: SUB_Offer_DefineFinalOfferStatus

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Retrieve
      - Store the result in a new variable called **$OfferItemList_1****
4. **Take the list **$OfferItemList_1**, perform a [FindByExpression] where: { $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or
$currentObject/BuyerCounterStatus=EcoATM_PWS.ENUM_CounterStatus.Accept }, and call the result **$ExistOfferItem****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Enumeration] result.
