# Microflow Analysis: SUB_CheckChangedEmail

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [
  (
    SubmissionID = $EcoATMDirectUser/SubmissionID
  )
] } (Call this list **$ExistingEcoATMDirectUser**)**
2. **Decision:** "email changed?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
