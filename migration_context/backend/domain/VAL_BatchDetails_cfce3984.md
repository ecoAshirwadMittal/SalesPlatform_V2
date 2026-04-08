# Microflow Analysis: VAL_BatchDetails

### Requirements (Inputs):
- **$IncomingEmailConfiguration** (A record of type: Email_Connector.IncomingEmailConfiguration)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Folder"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Handling"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "Check condition"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
5. **Decision:** "Move Folder"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Decision:** "MoveFolder != Folder"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
7. **Decision:** "Batch Size"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Decision:** "Nr of emails"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
9. **Decision:** "Timeout"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
