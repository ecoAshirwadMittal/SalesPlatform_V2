# Microflow Analysis: ACT_EcoATMDIrectUser_UpdateByAdmin

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$JSONContent****
2. **Run another process: "Custom_Logging.SUB_Log_Warning"**
3. **Permanently save **$undefined** to the database.**
4. **Run another process: "EcoATM_UserManagement.SUB_SetUserOwnerAndChanger"**
5. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList****
6. **Change List**
7. **Run another process: "EcoATM_UserManagement.SUB_SendUserToSnowflake"**
8. **Search the Database for **System.UserRole** using filter: { [
  (
    Name = 'Bidder'
  )
] } (Call this list **$BidderRole**)**
9. **Decision:** "is Bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
10. **Run another process: "EcoATM_UserManagement.SUB_SendBuyerUserToSnowflake"**
11. **Close Form**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
