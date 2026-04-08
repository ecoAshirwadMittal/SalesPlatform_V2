# Microflow Detailed Specification: ACT_OpenSite

### 📥 Inputs (Parameters)
- **$OpenSiteByIdRequest** (Type: Sharepoint.OpenSiteByIdRequest)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OpenSiteByIdRequest_Explorer** via Association from **$OpenSiteByIdRequest** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Call Microflow **Sharepoint.GetSite** (Result: **$Site**)**
4. **Update **$Explorer**
      - Set **SelectedSite** = `$Site`**
5. **Close current page/popup**
6. **Maps to Page: **Sharepoint.Site_Details****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.