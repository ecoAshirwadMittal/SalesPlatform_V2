# Microflow Analysis: SUB_ImportUpdatePODetails

### Requirements (Inputs):
- **$PurchaseOrder** (A record of type: EcoATM_PO.PurchaseOrder)
- **$POHelper** (A record of type: EcoATM_PO.POHelper)
- **$PurchaseOrderDoc** (A record of type: EcoATM_PO.PurchaseOrderDoc)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.POHelper.MissingBuyerCodeValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.MissingBuyerCodeList] to: "''
"
      - Change [EcoATM_PO.POHelper.InvalidFileValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InValidPOPeriod] to: "false
"**
3. **Import Excel Data
      - Store the result in a new variable called **$MWPO_REPORTList**** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "EcoATM_PO.VAL_BuyerCode_PO"
      - Store the result in a new variable called **$isBuyerCode_Valid****
5. **Decision:** "Are Buyer Codes valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$PODetailList_Old****
7. **Delete**
8. **Aggregate List
      - Store the result in a new variable called **$Count****
9. **Create List
      - Store the result in a new variable called **$PODetailList****
10. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { Show everything } (Call this list **$BuyerCodeList**)**
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Permanently save **$undefined** to the database.**
13. **Run another process: "EcoATM_PO.SUB_UploadPOToSnowFlake"**
14. **Close Form**
15. **Show Page**
16. **Show Message**
17. **Run another process: "EcoATM_PO.NAV_CreatePO"**
18. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
