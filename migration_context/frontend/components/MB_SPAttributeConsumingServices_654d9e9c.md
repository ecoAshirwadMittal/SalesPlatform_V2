# Microflow Analysis: MB_SPAttributeConsumingServices

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SPAttributeConsumingServiceList****
2. **Take the list **$SPAttributeConsumingServiceList**, perform a [Find] where: { SAML20.Enum_Attribute_Consuming_Login_Type.Initial_Login }, and call the result **$SPAttributeConsumingServiceInitialLogin****
3. **Run another process: "SAML20.ACT_AttributeConsumingService_Save"
      - Store the result in a new variable called **$SPAttributeConsumingServiceInitialLoginSaved****
4. **Take the list **$SPAttributeConsumingServiceList**, perform a [Find] where: { SAML20.Enum_Attribute_Consuming_Login_Type.InSession_Login }, and call the result **$SPAttributeConsumingServiceInSession****
5. **Run another process: "SAML20.ACT_AttributeConsumingService_Save"
      - Store the result in a new variable called **$SPAttributeConsumingServiceInSessionSaved****
6. **Decision:** "'Are  SPAttributeConsumingServices Saved?'"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
