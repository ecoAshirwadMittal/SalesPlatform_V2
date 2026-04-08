# Microflow Analysis: SUB_DeviceBuyer_SetClearingBId

### Requirements (Inputs):
- **$NPE_ClearingBid** (A record of type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)

### Execution Steps:
1. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [
EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation
and
AwardedQty != 0
]
 } (Call this list **$AwardedBids**)**
2. **Create Variable**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [
EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation
and
AwardedQty = 0
]
 } (Call this list **$NonAwardedBids**)**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
