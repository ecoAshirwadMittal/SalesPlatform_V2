# Microflow Analysis: SUB_ImportCreatePODetails

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
3. **Retrieve
      - Store the result in a new variable called **$FromWeek****
4. **Retrieve
      - Store the result in a new variable called **$ToWeek****
5. **Run another process: "EcoATM_PO.VAL_WeekRange_PO"
      - Store the result in a new variable called **$isWeekRange_Valid****
6. **Decision:** "Week range validation passed ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Import Excel Data
      - Store the result in a new variable called **$MWPO_REPORTList**** ⚠️ *(This step has a safety catch if it fails)*
8. **Run another process: "EcoATM_PO.VAL_BuyerCode_PO"
      - Store the result in a new variable called **$isBuyerCode_Valid****
9. **Decision:** "Are Buyer Codes valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Aggregate List
      - Store the result in a new variable called **$Count****
11. **Create List
      - Store the result in a new variable called **$PODetailList****
12. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { Show everything } (Call this list **$BuyerCodeList**)**
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.PurchaseOrder.TotalRecords] to: "$Count
"**
15. **Permanently save **$undefined** to the database.**
16. **Permanently save **$undefined** to the database.**
17. **Search the Database for **EcoATM_MDM.Week** using filter: { [$FromWeek/WeekStartDateTime<=WeekStartDateTime and $ToWeek/WeekStartDateTime>=WeekStartDateTime]
 } (Call this list **$NewPOWeekList**)**
18. **Create List
      - Store the result in a new variable called **$WeekPeriodList_toCommit****
19. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
20. **Permanently save **$undefined** to the database.**
21. **Run another process: "EcoATM_PO.SUB_UploadPOToSnowFlake"**
22. **Close Form**
23. **Show Page**
24. **Show Page**
25. **Run another process: "EcoATM_PO.NAV_CreatePO"**
26. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
27. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
