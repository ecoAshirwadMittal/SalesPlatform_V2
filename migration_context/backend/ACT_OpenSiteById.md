# Microflow Detailed Specification: ACT_OpenSiteById

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$Explorer** (Type: Sharepoint.Explorer)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Explorer**
      - Set **Explorer_Authorization** = `$Authorization`**
2. **Create **Sharepoint.OpenSiteByIdRequest** (Result: **$NewDirectSiteRequest**)
      - Set **OpenSiteByIdRequest_Explorer** = `$Explorer`**
3. **Maps to Page: **Sharepoint.OpenSiteByIdDialog****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.