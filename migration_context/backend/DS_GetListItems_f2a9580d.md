# Microflow Analysis: DS_GetListItems

### Requirements (Inputs):
- **$List** (A record of type: Sharepoint.List)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Authorization****
3. **Retrieve
      - Store the result in a new variable called **$Site****
4. **Run another process: "Sharepoint.GetListItems"
      - Store the result in a new variable called **$Result**** ⚠️ *(This step has a safety catch if it fails)*
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
