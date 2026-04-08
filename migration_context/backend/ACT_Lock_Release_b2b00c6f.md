# Microflow Analysis: ACT_Lock_Release

### Requirements (Inputs):
- **$Lock** (A record of type: EcoATM_Lock.Lock)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_Lock.Lock.Active] to: "false
"
      - **Save:** This change will be saved to the database immediately.**
2. **Show Message**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
