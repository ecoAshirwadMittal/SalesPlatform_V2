# Microflow Analysis: SUB_ReserveBid_DeleteAll

### Execution Steps:
1. **Search the Database for **EcoATM_EB.ReserveBid** using filter: { Show everything } (Call this list **$ReserveBidList**)**
2. **Decision:** "exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Delete**

**Conclusion:** This process sends back a [Void] result.
