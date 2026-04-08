# Microflow Analysis: ACT_ProcessDeepLinkForUserActivation

### Requirements (Inputs):
- **$GUID** (A record of type: Object)

### Execution Steps:
1. **Log Message**
2. **Search the Database for **ForgotPassword.ForgotPassword** using filter: { Show everything } (Call this list **$ForgotPasswordList**)**
3. **Create List
      - Store the result in a new variable called **$NewForgotPasswordList****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Decision:** "found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Take the list **$NewForgotPasswordList**, perform a [Head], and call the result **$ForgotPassword****
7. **Decision:** "Expired?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Is Sign-up**
8. **Log Message**
9. **Show Page**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
