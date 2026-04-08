# Microflow Analysis: DS_DeviceAllocation_CalculateClearingBid

### Requirements (Inputs):
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$DeviceBuyerList****
3. **Create Object
      - Store the result in a new variable called **$NewNPE_ClearingBid****
4. **Decision:** "Device Buyer not empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [
EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation
and
ClearingBid = true
]
 } (Call this list **$ClearingBidDevuceBuyer**)**
6. **Decision:** "Clearing Bid not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
7. **Run another process: "EcoATM_DA.SUB_DeviceBuyer_CalculateLastAwardedBid"**
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
