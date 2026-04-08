# Microflow Analysis: DS_DABuyerCodes

### Requirements (Inputs):
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [EcoATM_DA.DeviceBuyer_DAWeek = $DAHelper/EcoATM_DA.DAHelper_DAWeek]

 } (Call this list **$DeviceBuyerList**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
