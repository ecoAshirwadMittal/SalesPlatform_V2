# Microflow Analysis: ACT_LoadDAData

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Decision:** "isDAHelper not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$DAWeek****
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Execute Database Query
      - Store the result in a new variable called **$DAUploadTime**** ⚠️ *(This step has a safety catch if it fails)*
5. **Take the list **$DAUploadTime**, perform a [Head], and call the result **$NewDAUploadTime****
6. **Create Variable**
7. **Decision:** "is LastUploadTime in DA week empty?"
   - If [true] -> Move to: **is Upload time newer?**
   - If [false] -> Move to: **Activity**
8. **Decision:** "is Upload time newer?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
9. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAHelper.DisplayDA_DataGrid] to: "true
"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
