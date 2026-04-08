# Microflow Analysis: ACT_CreateListItemCreate

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
5. **Retrieve
      - Store the result in a new variable called **$Fields****
6. **Run another process: "Sharepoint.CreateListItem"
      - Store the result in a new variable called **$NewId**** ⚠️ *(This step has a safety catch if it fails)*
7. **Show Message**
8. **Close Form**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
