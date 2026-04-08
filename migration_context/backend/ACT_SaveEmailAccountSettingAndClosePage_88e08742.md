# Microflow Analysis: ACT_SaveEmailAccountSettingAndClosePage

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Decision:** "If send and recieve both not checked"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Show Message**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
