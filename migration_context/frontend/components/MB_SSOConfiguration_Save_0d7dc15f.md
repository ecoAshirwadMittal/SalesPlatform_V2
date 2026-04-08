# Microflow Analysis: MB_SSOConfiguration_Save

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration.IdPMetadataURL] to: "if $SSOConfiguration/ReadIdPMetadataFromURL = false then
	''
else
	$SSOConfiguration/IdPMetadataURL"**
2. **Search the Database for **SAML20.SSOConfiguration** using filter: { [Alias = $SSOConfiguration/Alias and id != $SSOConfiguration] } (Call this list **$SameAliasSSOConfigurationList**)**
3. **Aggregate List
      - Store the result in a new variable called **$SSOCountWithSameAlias****
4. **Decision:** "is duplicate alias?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Run another process: "SAML20.MB_IDP_SelfGenerate_KeyStore"**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "SAML20.MB_SPAttributeConsumingServices"
      - Store the result in a new variable called **$IsSPAttributeConsumingServiceSaved****
8. **Decision:** "'is SPAttributeConsumingService Saved?'"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
9. **Close Form**
10. **Java Action Call
      - Store the result in a new variable called **$unused****
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
