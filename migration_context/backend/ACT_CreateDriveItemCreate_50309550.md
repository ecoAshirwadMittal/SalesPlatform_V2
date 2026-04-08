# Microflow Analysis: ACT_CreateDriveItemCreate

### Requirements (Inputs):
- **$FileDocument** (A record of type: System.FileDocument)
- **$Auction** (A record of type: AuctionUI.Auction)
- **$AuctionSubFolderName** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Decision:** "Enabled?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Run another process: "EcoATM_Direct_Sharepoint.ACT_GetAuthorization"
      - Store the result in a new variable called **$Authorization****
8. **Create Variable**
9. **Run another process: "Sharepoint.GetDrives"
      - Store the result in a new variable called **$SiteDrives**** ⚠️ *(This step has a safety catch if it fails)*
10. **Take the list **$SiteDrives**, perform a [Find] where: { 'Documents' }, and call the result **$Drive****
11. **Decision:** "Drive?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
12. **Run another process: "Sharepoint.CreateDriveItem"
      - Store the result in a new variable called **$DriveItemId**** ⚠️ *(This step has a safety catch if it fails)*
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
