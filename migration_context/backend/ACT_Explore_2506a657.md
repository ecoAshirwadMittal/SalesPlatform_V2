# Microflow Analysis: ACT_Explore

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$Explorer** (A record of type: Sharepoint.Explorer)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [Sharepoint.Explorer_Authorization] to: "$Authorization"**
2. **Run another process: "Sharepoint.SearchSites"
      - Store the result in a new variable called **$SearchSitesResult**** ⚠️ *(This step has a safety catch if it fails)*
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
