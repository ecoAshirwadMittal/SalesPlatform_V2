# Microflow Analysis: VAL_Device

### Requirements (Inputs):
- **$Device** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.SUB_Notes_RetrieveOrCreate"
      - Store the result in a new variable called **$DeviceNote****
2. **Create Variable**
3. **Create Variable**
4. **Decision:** "Current Price"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Change Variable**
6. **Decision:** "Current Min"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Change Variable**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWSMDM.Note.Notes] to: "$Notes
"
      - **Save:** This change will be saved to the database immediately.**
9. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
10. **Permanently save **$undefined** to the database.**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
