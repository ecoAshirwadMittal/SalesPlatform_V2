# Microflow Analysis: SUB_Offer_PrepareOraclePayload

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$Order** (A record of type: EcoATM_PWS.Order)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer=$Offer]
[SalesOfferItemStatus='Accept' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept') or SalesOfferItemStatus='Finalize' ] } (Call this list **$AcceptedOfferItemList**)**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
5. **Create Object
      - Store the result in a new variable called **$NewOracleRequest****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
