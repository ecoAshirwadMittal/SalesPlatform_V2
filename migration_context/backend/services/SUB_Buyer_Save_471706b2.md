# Microflow Analysis: SUB_Buyer_Save

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Run another process: "EcoATM_MDM.VAL_Buyer"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "Buyer name valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
5. **Take the list **$BuyerCodeList**, perform a [Filter] where: { true }, and call the result **$BuyerCodeList_Delete****
6. **Take the list **$BuyerCodeList**, perform a [Subtract], and call the result **$BuyerCodeList_toCommit****
7. **Delete**
8. **Run another process: "EcoATM_MDM.VAL_BuyerCode"
      - Store the result in a new variable called **$Buyercodes_valid****
9. **Decision:** "buyer codes valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Aggregate List
      - Store the result in a new variable called **$ReducedBuyerCodes****
11. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.BuyerCodesDisplay] to: "$ReducedBuyerCodes"**
12. **Run another process: "EcoATM_MDM.SUB_SetBuyerOwnerAndChanger"**
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Permanently save **$undefined** to the database.**
15. **Permanently save **$undefined** to the database.**
16. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
17. **Decision:** "send buyer to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
18. **Create List
      - Store the result in a new variable called **$BuyerList****
19. **Change List**
20. **Run another process: "EcoATM_MDM.SUB_SendBuyerToSnowflake"** ⚠️ *(This step has a safety catch if it fails)*
21. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
22. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
