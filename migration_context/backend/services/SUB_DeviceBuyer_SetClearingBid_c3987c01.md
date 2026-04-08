# Microflow Analysis: SUB_DeviceBuyer_SetClearingBid

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
2. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [
EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation
and
AwardedQty = 0
and
QtyCap = empty
]
 } (Call this list **$NonAwardedBids**)**
3. **Take the list **$NonAwardedBids**, perform a [Subtract], and call the result **$NewDeviceBuyerList****
4. **Take the list **$NewDeviceBuyerList**, perform a [Sort], and call the result **$NewDeviceBuyerList_2****
5. **Take the list **$NewDeviceBuyerList_2**, perform a [Head], and call the result **$NewDeviceBuyer****
6. **Decision:** "new device buyer not empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceBuyer.ClearingBid] to: "true
"
      - **Save:** This change will be saved to the database immediately.**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.NPE_ClearingBid.ClearingBid] to: "toString($NewDeviceBuyer/Bid)
"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
