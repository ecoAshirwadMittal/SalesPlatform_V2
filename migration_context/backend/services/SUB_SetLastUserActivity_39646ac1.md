# Microflow Analysis: SUB_SetLastUserActivity

### Requirements (Inputs):
- **$IdleTimeout** (A record of type: EcoATM_Direct_Theme.IdleTimeout)

### Execution Steps:
1. **Decision:** "Not Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$IdleTimeoutList****
3. **Take the list **$IdleTimeoutList**, perform a [Sort], and call the result **$IdleTimeoutList_Sorted****
4. **Take the list **$IdleTimeoutList_Sorted**, perform a [Head], and call the result **$IdleTimeout_Existing****
5. **Update the **$undefined** (Object):
      - Change [EcoATM_Direct_Theme.IdleTimeout.LastRecordedActivity] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
