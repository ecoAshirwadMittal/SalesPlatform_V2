# Microflow Analysis: ACT_Lock_Refresh

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$ObjectInfo****
2. **Decision:** "found?"
   - If [true] -> Move to: **User allowred?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "User allowred?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
