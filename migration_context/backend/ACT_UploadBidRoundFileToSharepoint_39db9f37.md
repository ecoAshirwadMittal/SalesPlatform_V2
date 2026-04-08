# Microflow Analysis: ACT_UploadBidRoundFileToSharepoint

### Execution Steps:
1. **Run another process: "EcoATM_Direct_Sharepoint.ACT_GetAuthorization"
      - Store the result in a new variable called **$Authorization****
2. **Create Variable**
3. **Run another process: "Sharepoint.GetSite"
      - Store the result in a new variable called **$Site**** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "Sharepoint.GetLists"
      - Store the result in a new variable called **$Result**** ⚠️ *(This step has a safety catch if it fails)*
5. **Take the list **$Result**, perform a [Find], and call the result **$NewList****
6. **Run another process: "Sharepoint.ACT_CreateDriveItem"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
