# Microflow Analysis: VAL_OrderStatus_IsValide

### Requirements (Inputs):
- **$OrderStatus** (A record of type: EcoATM_PWS.OrderStatus)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { [SystemStatus=$OrderStatus/SystemStatus]
[id!=$OrderStatus]
 } (Call this list **$AltreadyExistOrderStatus**)**
2. **Decision:** "new?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
