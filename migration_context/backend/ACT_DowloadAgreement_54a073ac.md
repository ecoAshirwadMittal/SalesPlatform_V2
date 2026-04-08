# Microflow Analysis: ACT_DowloadAgreement

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_MDM.UserHelperGuide** using filter: { [
  (
    GuideType = 'Sales_Platform_Terms'
    and Active = true()
  )
] } (Call this list **$UserHelperGuide**)**
5. **Decision:** "?exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Download File**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
