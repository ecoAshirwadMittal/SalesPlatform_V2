# Microflow Analysis: SUB_LoadDAData

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Execute Database Query
      - Store the result in a new variable called **$DAUploadTime**** ⚠️ *(This step has a safety catch if it fails)*
2. **Take the list **$DAUploadTime**, perform a [Head], and call the result **$NewDAUploadTime****
3. **Decision:** "Max Upload Time Available?"
   - If [true] -> Move to: **is Upload time newer?**
   - If [false] -> Move to: **Finish**
4. **Decision:** "is Upload time newer?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAWeek.LastRefreshTime] to: "[%CurrentDateTime%]
"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
