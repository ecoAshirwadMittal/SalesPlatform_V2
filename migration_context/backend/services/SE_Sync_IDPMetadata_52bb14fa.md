# Microflow Analysis: SE_Sync_IDPMetadata

### Execution Steps:
1. **Search the Database for **SAML20.SSOConfiguration** using filter: { [ReadIdPMetadataFromURL=true()]
[Active=true()] } (Call this list **$SSOConfiguration**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
