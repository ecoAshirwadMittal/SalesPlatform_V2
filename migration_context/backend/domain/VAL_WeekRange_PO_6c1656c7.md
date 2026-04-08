# Microflow Analysis: VAL_WeekRange_PO

### Requirements (Inputs):
- **$PurchaseOrder** (A record of type: EcoATM_PO.PurchaseOrder)
- **$ToWeek** (A record of type: EcoATM_MDM.Week)
- **$FromWeek** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "FromWeek exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "ToWeek exists AND ToWeek>FromWeek?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "ValidationPassed"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Search the Database for **EcoATM_PO.WeekPeriod** using filter: { [$FromWeek/WeekStartDateTime<=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime and $ToWeek/WeekStartDateTime>=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime]
 } (Call this list **$WeekPeriodList**)**
6. **Decision:** "Week Valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
