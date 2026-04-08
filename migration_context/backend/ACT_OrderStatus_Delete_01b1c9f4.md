# Microflow Analysis: ACT_OrderStatus_Delete

### Requirements (Inputs):
- **$OrderStatus** (A record of type: EcoATM_PWS.OrderStatus)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus=$OrderStatus]
 } (Call this list **$OfferList**)**
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Decision:** "used?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Delete**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
