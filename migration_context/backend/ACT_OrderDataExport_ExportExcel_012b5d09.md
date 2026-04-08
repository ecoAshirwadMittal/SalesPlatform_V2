# Microflow Analysis: ACT_OrderDataExport_ExportExcel

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Retrieve
      - Store the result in a new variable called **$OrderItemList****
6. **Decision:** "Has OrderItems?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Take the list **$OrderItemList**, perform a [FilterByExpression] where: { $currentObject/Quantity != empty
and
$currentObject/Quantity > 0 }, and call the result **$OrderItemList_Valid****
8. **Decision:** "OrderItems Qty exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'PWSOrder']
 } (Call this list **$MxTemplate**)**
10. **Create Object
      - Store the result in a new variable called **$NewOrderDataDoc****
11. **Create List
      - Store the result in a new variable called **$OrderDataExcelList****
12. **Create Variable**
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Permanently save **$undefined** to the database.**
15. **Create Variable**
16. **Run another process: "EcoATM_PWS.SUB_OrderData_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
17. **Delete**
18. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
