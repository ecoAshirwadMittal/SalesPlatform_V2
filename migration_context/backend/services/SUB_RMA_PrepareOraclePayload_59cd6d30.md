# Microflow Analysis: SUB_RMA_PrepareOraclePayload

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Retrieve
      - Store the result in a new variable called **$RMAItemList****
4. **Take the list **$RMAItemList**, perform a [Filter] where: { EcoATM_RMA.ENUM_RMAItemStatus.Approve }, and call the result **$RMAItemList_Approved****
5. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
6. **Create Object
      - Store the result in a new variable called **$NewRMARequest****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
