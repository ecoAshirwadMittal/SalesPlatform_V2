# Microflow Analysis: SUB_ResetUserAcknowledgement

### Execution Steps:
1. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { Show everything } (Call this list **$EcoATMDirectUserList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
