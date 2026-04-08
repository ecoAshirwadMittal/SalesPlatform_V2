# Microflow Analysis: VAL_ChargeOfferStatusHelper_IsValid

### Requirements (Inputs):
- **$ChangeOfferStatusHelper** (A record of type: EcoATM_PWS.ChangeOfferStatusHelper)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "All Periods?"
   - If [true] -> Move to: **selected orders?**
   - If [false] -> Move to: **start date?**
3. **Decision:** "selected orders?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "From and to Status"
   - If [false] -> Move to: **To status not empty?**
   - If [true] -> Move to: **Finish**
5. **Decision:** "To status not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
6. **Change Variable**
7. **Validation Feedback**
8. **Decision:** "validate both?"
   - If [true] -> Move to: **from status not empty?**
   - If [false] -> Move to: **Finish**
9. **Decision:** "from status not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
10. **Change Variable**
11. **Validation Feedback**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
