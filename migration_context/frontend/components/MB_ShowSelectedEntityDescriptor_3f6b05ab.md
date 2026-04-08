# Microflow Analysis: MB_ShowSelectedEntityDescriptor

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Decision:** "has entity descriptor"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Retrieve
      - Store the result in a new variable called **$EntityDescriptor****
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
