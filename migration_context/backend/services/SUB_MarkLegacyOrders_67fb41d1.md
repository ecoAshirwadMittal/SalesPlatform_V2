# Microflow Analysis: SUB_MarkLegacyOrders

### Requirements (Inputs):
- **$LegacyOrderDateHelper** (A record of type: EcoATM_PWS.LegacyOrderDateHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Decision:** "Date empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Search the Database for **EcoATM_PWS.Order** using filter: { Show everything } (Call this list **$OrderList**)**
6. **Decision:** "Orders exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Take the list **$OrderList**, perform a [FilterByExpression] where: { trimToDays($currentObject/createdDate) <= trimToDays($LegacyOrderDateHelper/LegacyDate) }, and call the result **$OrderList_filtered****
8. **Decision:** "Filtered Orders exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
9. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { [SystemStatus ='Shipped'] } (Call this list **$OrderStatus_Shipped**)**
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Permanently save **$undefined** to the database.**
12. **Close Form**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
