# Microflow Detailed Specification: ACT_Explore

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$Explorer** (Type: Sharepoint.Explorer)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Explorer**
      - Set **Explorer_Authorization** = `$Authorization`**
2. **Call Microflow **Sharepoint.SearchSites** (Result: **$SearchSitesResult**)**
3. **Maps to Page: **Sharepoint.SearchSitesResult****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.