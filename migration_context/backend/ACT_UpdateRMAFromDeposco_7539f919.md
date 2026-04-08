# Microflow Analysis: ACT_UpdateRMAFromDeposco

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Search the Database for **EcoATM_RMA.RMA** using filter: { [SystemStatus!='Canceled' and SystemStatus!='Submitted' and SystemStatus!='Received']
[OracleNumber!=empty]
 } (Call this list **$RMAList**)**
4. **Aggregate List
      - Store the result in a new variable called **$Count****
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Search the Database for **EcoATM_PWSIntegration.DeposcoConfig** using filter: { Show everything } (Call this list **$DeposcoConfig**)**
7. **Retrieve
      - Store the result in a new variable called **$DesposcoAPIsList****
8. **Take the list **$DesposcoAPIsList**, perform a [FindByExpression] where: { $currentObject/ServiceName = EcoATM_PWSIntegration.ENUM_DeposcoServices.RMA }, and call the result **$DesposcoAPIsForOrderHistory****
9. **Search the Database for **EcoATM_RMA.RMAStatus** using filter: { Show everything } (Call this list **$RMAStatusList**)**
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Permanently save **$undefined** to the database.**
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
