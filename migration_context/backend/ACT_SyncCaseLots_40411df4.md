# Microflow Analysis: ACT_SyncCaseLots

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_PWSIntegration.DeposcoConfig** using filter: { Show everything } (Call this list **$DeposcoConfig**)**
5. **Retrieve
      - Store the result in a new variable called **$DesposcoAPIsList****
6. **Take the list **$DesposcoAPIsList**, perform a [Find] where: { EcoATM_PWSIntegration.ENUM_DeposcoServices.StockUnit }, and call the result **$DesposcoAPI****
7. **Create Variable**
8. **Run another process: "EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword"
      - Store the result in a new variable called **$EncodedAuth****
9. **Java Action Call
      - Store the result in a new variable called **$DeleteStaleRecords****
10. **Create List
      - Store the result in a new variable called **$StockUnitItemList****
11. **Create Variable**
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Permanently save **$undefined** to the database.**
14. **Java Action Call
      - Store the result in a new variable called **$ForceCommit****
15. **Create Variable**
16. **Java Action Call
      - Store the result in a new variable called **$CaseLots****
17. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [ItemType='SPB']
 } (Call this list **$CaseLotDevices**)**
18. **Create List
      - Store the result in a new variable called **$CaseLotList_ToCommit****
19. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
20. **Permanently save **$undefined** to the database.**
21. **Search the Database for **EcoATM_PWSMDM.CaseLot** using filter: { [changedDate<$JobStartTime]
[IsActive]
 } (Call this list **$CaseLotList_InActive**)**
22. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
23. **Permanently save **$undefined** to the database.**
24. **Run another process: "EcoATM_PWS.SUB_UpdateReservedQuanityPerCaseLot"**
25. **Run another process: "EcoATM_PWS.SUB_SendCaseLotsToSnowflakeInitiate"**
26. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
27. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
