# Microflow Analysis: ACT_DownloadItem

### Requirements (Inputs):
- **$ListItem** (A record of type: Sharepoint.ListItem)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Authorization****
3. **Retrieve
      - Store the result in a new variable called **$Site****
4. **Retrieve
      - Store the result in a new variable called **$List****
5. **Run another process: "Sharepoint.DownloadDriveItem"
      - Store the result in a new variable called **$Item**** ⚠️ *(This step has a safety catch if it fails)*
6. **Update the **$undefined** (Object):
      - Change [System.FileDocument.DeleteAfterDownload] to: "true"
      - **Save:** This change will be saved to the database immediately.**
7. **Download File**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
