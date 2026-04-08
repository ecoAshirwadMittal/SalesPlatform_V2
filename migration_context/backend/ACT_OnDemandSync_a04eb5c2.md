# Microflow Analysis: ACT_OnDemandSync

### Execution Steps:
1. **Search the Database for **EcoATM_MDM.Week** using filter: { [WeekStartDateTime<='[%CurrentDateTime%]' and WeekEndDateTime >'[%CurrentDateTime%]']
 } (Call this list **$Week**)**
2. **Run another process: "EcoATM_BidData.SUB_GetCurrentWeekMinusOne"
      - Store the result in a new variable called **$Last_Week****
3. **Decision:** "Week exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Search the Database for **EcoATM_PO.PurchaseOrder** using filter: { [EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/WeekStartDateTime <= $Last_Week/WeekStartDateTime and EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/WeekEndDateTime >=  $Last_Week/WeekEndDateTime]
 } (Call this list **$PurchaseOrder**)**
5. **Decision:** "PurchaseOrder exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Retrieve
      - Store the result in a new variable called **$PODetailList****
7. **Decision:** "PODetailList has items?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
