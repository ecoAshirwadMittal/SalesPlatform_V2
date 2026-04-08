# Microflow Analysis: ACT_SPRequestedAttribute_Delete

### Requirements (Inputs):
- **$SPRequestedAttribute** (A record of type: SAML20.SPRequestedAttribute)
- **$SPAttributeConsumingService** (A record of type: SAML20.SPAttributeConsumingService)
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SPRequestedAttributeList****
2. **Aggregate List
      - Store the result in a new variable called **$AttributeCount****
3. **Create Variable**
4. **Decision:** "'is empty requested attribute?'"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Delete**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
