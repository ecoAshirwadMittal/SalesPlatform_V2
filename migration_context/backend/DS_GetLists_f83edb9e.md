# Microflow Analysis: DS_GetLists

### Requirements (Inputs):
- **$Site** (A record of type: Sharepoint.Site)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Authorization****
3. **Run another process: "Sharepoint.GetLists"
      - Store the result in a new variable called **$Result**** ⚠️ *(This step has a safety catch if it fails)*
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
