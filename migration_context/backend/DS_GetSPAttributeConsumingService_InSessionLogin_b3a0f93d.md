# Microflow Analysis: DS_GetSPAttributeConsumingService_InSessionLogin

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SPAttributeConsumingServiceList****
2. **Take the list **$SPAttributeConsumingServiceList**, perform a [Find] where: { SAML20.Enum_Attribute_Consuming_Login_Type.InSession_Login }, and call the result **$SPAttributeConsumingServiceIndex****
3. **Decision:** "is SPAttributeConsumingService empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Create Object
      - Store the result in a new variable called **$NewSPAttributeConsumingService****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
