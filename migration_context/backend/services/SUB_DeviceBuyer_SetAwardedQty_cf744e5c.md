# Microflow Analysis: SUB_DeviceBuyer_SetAwardedQty

### Requirements (Inputs):
- **$JsonString** (A record of type: Object)
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$NPE_ClearingBid** (A record of type: EcoATM_DA.NPE_ClearingBid)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "Json not empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Create Variable**
5. **Import Xml**
6. **Retrieve
      - Store the result in a new variable called **$JsonObjectList****
7. **Create List
      - Store the result in a new variable called **$DeviceBuyerListToCommit****
8. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [
EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation
and
BuyerCode = 'EB'
]
 } (Call this list **$EBBuyer**)**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceBuyer.AwardedQty] to: "$AvailableQuantity
"**
11. **Change List**
12. **Permanently save **$undefined** to the database.**
13. **Run another process: "Custom_Logging.SUB_Log_Info"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
