# Microflow Analysis: SUB_Offer_PrepareOraclePayload_2

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$Order** (A record of type: EcoATM_PWS.Order)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
3. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
4. **Create Object
      - Store the result in a new variable called **$NewOracleRequest****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
