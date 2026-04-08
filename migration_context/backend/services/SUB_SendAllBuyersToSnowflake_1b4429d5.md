# Microflow Analysis: SUB_SendAllBuyersToSnowflake

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { Show everything } (Call this list **$BuyerList**)**
2. **Create List
      - Store the result in a new variable called **$ToBeCommitedBuyerList****
3. **Create List
      - Store the result in a new variable called **$ToBeCommitedBuyerCodeList****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Decision:** "BuyerCodes?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Permanently save **$undefined** to the database.**
7. **Decision:** "Buyers?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Permanently save **$undefined** to the database.**
9. **Run another process: "EcoATM_MDM.SUB_SendBuyerToSnowflake"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
