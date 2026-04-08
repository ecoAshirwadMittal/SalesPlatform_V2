# Microflow Analysis: VAL_EmailTemplateRecipients

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "From filled in?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
3. **Decision:** "Valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "To filled in?"
   - If [true] -> Move to: **Valid?**
   - If [false] -> Move to: **Finish**
5. **Decision:** "Valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
6. **Change Variable**
7. **Validation Feedback**
8. **Decision:** "Reply to filled in?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Valid**
9. **Decision:** "Cc filled in?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Valid**
10. **Decision:** "BCC filled in?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Valid**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
