# Microflow Analysis: ACT_OpenSite

### Requirements (Inputs):
- **$OpenSiteByIdRequest** (A record of type: Sharepoint.OpenSiteByIdRequest)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Explorer****
2. **Retrieve
      - Store the result in a new variable called **$Authorization****
3. **Run another process: "Sharepoint.GetSite"
      - Store the result in a new variable called **$Site**** ⚠️ *(This step has a safety catch if it fails)*
4. **Update the **$undefined** (Object):
      - Change [Sharepoint.SelectedSite] to: "$Site"**
5. **Close Form**
6. **Show Page**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
