# Microflow Analysis: ACT_AttributeConsumingService_Save

### Requirements (Inputs):
- **$SPAttributeConsumingService** (A record of type: SAML20.SPAttributeConsumingService)
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Search the Database for **SAML20.SPAttributeConsumingService** using filter: { [SAML20.SPAttributeConsumingService_SSOConfiguration = $SSOConfiguration 
and id != $SPAttributeConsumingService/id]  } (Call this list **$SPAttributeConsumingServiceList**)**
2. **Take the list **$SPAttributeConsumingServiceList**, perform a [Find] where: { $SPAttributeConsumingService/index }, and call the result **$NewSPAttributeConsumingService****
3. **Decision:** "Is Index duplicate?"
   - If [true] -> Move to: **is SPAttributeConsumingService Active?**
   - If [false] -> Move to: **Activity**
4. **Decision:** "is SPAttributeConsumingService Active?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
5. **Permanently save **$undefined** to the database.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
