# Microflow Analysis: ACT_OrderHistory_ExportExcel

### Requirements (Inputs):
- **$OrderHistoryHelper** (A record of type: EcoATM_PWS.OrderHistoryHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Create List
      - Store the result in a new variable called **$OfferAndOrdersViewList****
5. **Decision:** "Curret Tab?"
   - If [All] -> Move to: **Activity**
   - If [Recent] -> Move to: **Activity**
   - If [InProcess] -> Move to: **Activity**
   - If [Complete] -> Move to: **Activity**
   - If [(empty)] -> Move to: **Activity**
6. **Search the Database for **EcoATM_PWS.OfferAndOrdersView** using filter: { Show everything } (Call this list **$OfferAndOrdersViewList_ALL**)**
7. **Change List**
8. **Decision:** "Has OfferandOrderViewList?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
