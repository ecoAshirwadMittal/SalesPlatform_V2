# Microflow Analysis: ACT_CreateDriveItemCreate

### Requirements (Inputs):
- **$CreateDriveItemHelper** (A record of type: Sharepoint.CreateDriveItemHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$DriveItem****
2. **Retrieve
      - Store the result in a new variable called **$Explorer****
3. **Retrieve
      - Store the result in a new variable called **$Authorization****
4. **Retrieve
      - Store the result in a new variable called **$DriveItemContents****
5. **Retrieve
      - Store the result in a new variable called **$ListItem****
6. **Retrieve
      - Store the result in a new variable called **$Fields****
7. **Run another process: "Sharepoint.CreateDriveItem"
      - Store the result in a new variable called **$DriveItemId**** ⚠️ *(This step has a safety catch if it fails)*
8. **Run another process: "Sharepoint.GetDriveItem"
      - Store the result in a new variable called **$NewDriveItem**** ⚠️ *(This step has a safety catch if it fails)*
9. **Retrieve
      - Store the result in a new variable called **$NewListItem****
10. **Run another process: "Sharepoint.UpdateListItemFields"**
11. **Show Message**
12. **Close Form**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
