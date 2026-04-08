# Microflow Analysis: SUB_DeviceBuyer_CalculateLastAwardedBid

### Requirements (Inputs):
- **$NPE_ClearingBid** (A record of type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Create Variable**
3. **Search the Database for **EcoATM_EB.ReserveBid** using filter: { [
ProductId = $DeviceAllocation/ProductID
and
Grade = $DeviceAllocation/Grade
]
 } (Call this list **$ReserveBidList**)**
4. **Decision:** "reserve bids found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
