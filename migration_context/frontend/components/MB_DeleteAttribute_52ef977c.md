# Microflow Analysis: MB_DeleteAttribute

### Requirements (Inputs):
- **$Attribute** (A record of type: SAML20.Attribute)

### Execution Steps:
1. **Decision:** "Manually created?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Delete**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
