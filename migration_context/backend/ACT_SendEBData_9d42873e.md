# Microflow Analysis: ACT_SendEBData

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Create Variable**
3. **Create Variable**
4. **Retrieve
      - Store the result in a new variable called **$DeviceAllocationList****
5. **Decision:** "DA list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Retrieve
      - Store the result in a new variable called **$DeviceBuyerList****
7. **Decision:** "Buyer list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Create Object
      - Store the result in a new variable called **$NewRoot****
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Export Xml**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
