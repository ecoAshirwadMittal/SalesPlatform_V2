# Microflow Analysis: ACT_OpenListItem

### Requirements (Inputs):
- **$ListItem** (A record of type: Sharepoint.ListItem)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Site****
3. **Retrieve
      - Store the result in a new variable called **$List****
4. **Retrieve
      - Store the result in a new variable called **$Authorization****
5. **Run another process: "Sharepoint.GetListItem"
      - Store the result in a new variable called **$Item**** ⚠️ *(This step has a safety catch if it fails)*
6. **Update the **$undefined** (Object):
      - Change [Sharepoint.ListItem_Explorer] to: "$Explorer"**
7. **Show Page**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
