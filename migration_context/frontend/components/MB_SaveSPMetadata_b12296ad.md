# Microflow Analysis: MB_SaveSPMetadata

### Requirements (Inputs):
- **$SPMetadata** (A record of type: SAML20.SPMetadata)

### Execution Steps:
1. **Decision:** "Check condition"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
2. **Update the **$undefined** (Object):
      - Change [SAML20.SPMetadata.EntityID] to: "$SPMetadata/ApplicationURL"**
3. **Run another process: "SAML20.SPMetadata_Validate"
      - Store the result in a new variable called **$Valid****
4. **Decision:** "Valid"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Permanently save **$undefined** to the database.**
6. **Java Action Call
      - Store the result in a new variable called **$unused**** ⚠️ *(This step has a safety catch if it fails)*
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
