# Microflow Analysis: SUB_RetrieveOrderStatus

### Requirements (Inputs):
- **$ENUM_PWSOrderStatus** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { [SystemStatus=$StatusParam]
 } (Call this list **$OrderStatus**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
