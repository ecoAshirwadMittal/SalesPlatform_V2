# Microflow Analysis: DS_GetDriveItemsForDriveItem

### Requirements (Inputs):
- **$DriveItem** (A record of type: Sharepoint.DriveItem)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Authorization****
3. **Retrieve
      - Store the result in a new variable called **$Drive****
4. **Run another process: "Sharepoint.GetDriveItems"
      - Store the result in a new variable called **$Result**** ⚠️ *(This step has a safety catch if it fails)*
5. **Create Object
      - Store the result in a new variable called **$NewChildren****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
