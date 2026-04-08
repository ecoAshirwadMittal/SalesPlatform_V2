# Microflow Analysis: DS_GetDriveItemsForDrive

### Requirements (Inputs):
- **$Drive** (A record of type: Sharepoint.Drive)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Authorization****
3. **Run another process: "Sharepoint.GetDriveItems"
      - Store the result in a new variable called **$DriveItemList**** ⚠️ *(This step has a safety catch if it fails)*
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
