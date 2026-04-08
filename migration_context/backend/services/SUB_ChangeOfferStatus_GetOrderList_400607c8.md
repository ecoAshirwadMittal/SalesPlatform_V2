# Microflow Analysis: SUB_ChangeOfferStatus_GetOrderList

### Requirements (Inputs):
- **$ChangeOfferStatusHelper** (A record of type: EcoATM_PWS.ChangeOfferStatusHelper)

### Execution Steps:
1. **Decision:** "All period?"
   - If [true] -> Move to: **order exists?**
   - If [false] -> Move to: **Activity**
2. **Decision:** "order exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Retrieve
      - Store the result in a new variable called **$OrderList****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
