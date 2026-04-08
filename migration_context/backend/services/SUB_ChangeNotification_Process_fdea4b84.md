# Microflow Analysis: SUB_ChangeNotification_Process

### Requirements (Inputs):
- **$httpRequest** (A record of type: System.HttpRequest)

### Execution Steps:
1. **Import Xml**
2. **Log Message**
3. **Retrieve
      - Store the result in a new variable called **$ValidationTokens****
4. **Decision:** "Check if "ValidationTokens" is empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Sub microflow**
5. **Retrieve
      - Store the result in a new variable called **$Value****
6. **Retrieve
      - Store the result in a new variable called **$ChangeNotificationList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Decision:** "Check if "Subscription" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Log Message**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
