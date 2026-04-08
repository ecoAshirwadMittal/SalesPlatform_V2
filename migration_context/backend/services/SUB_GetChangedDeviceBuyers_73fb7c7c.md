# Microflow Analysis: SUB_GetChangedDeviceBuyers

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [EcoATM_DA.DeviceBuyer_DAWeek = $DAWeek]
[IsChanged]
[not(EB)]
 } (Call this list **$DeviceBuyerList**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
