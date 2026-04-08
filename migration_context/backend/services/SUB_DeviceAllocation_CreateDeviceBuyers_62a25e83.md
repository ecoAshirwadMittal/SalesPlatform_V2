# Microflow Analysis: SUB_DeviceAllocation_CreateDeviceBuyers

### Requirements (Inputs):
- **$BidDataList** (A record of type: AuctionUI.BidData)
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$DeviceBuyerListToCommit****
2. **Create Variable**
3. **Import Xml**
4. **Retrieve
      - Store the result in a new variable called **$NP_DeviceBuyerList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Create Object
      - Store the result in a new variable called **$ReserveBidToDeviceBuyer****
7. **Change List**
8. **Permanently save **$undefined** to the database.**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
