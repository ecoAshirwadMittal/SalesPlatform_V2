# Microflow Analysis: ACT_CreateTestUsersStart

### Requirements (Inputs):
- **$TestUserHelper** (A record of type: EcoATM_UserManagement.TestUserHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Decision:** "From <= To"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Create List
      - Store the result in a new variable called **$BuyerCodeList****
6. **Create List
      - Store the result in a new variable called **$BuyerList****
7. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList****
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Decision:** "new users are created?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Permanently save **$undefined** to the database.**
11. **Permanently save **$undefined** to the database.**
12. **Permanently save **$undefined** to the database.**
13. **Close Form**
14. **Show Message**
15. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
