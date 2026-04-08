# Microflow Analysis: ACT_LoadDAData_BKP

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Decision:** "isDAHelper not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$DAWeek****
3. **Execute Database Query
      - Store the result in a new variable called **$DAUploadTime****
4. **Take the list **$DAUploadTime**, perform a [Head], and call the result **$NewDAUploadTime****
5. **Create Variable**
6. **Decision:** "is LastUploadTime in DA week empty?"
   - If [true] -> Move to: **is Upload time newer?**
   - If [false] -> Move to: **Activity**
7. **Decision:** "is Upload time newer?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAHelper.DisplayDA_DataGrid] to: "true
"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
