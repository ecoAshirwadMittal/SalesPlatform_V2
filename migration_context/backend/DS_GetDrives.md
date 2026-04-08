# Microflow Detailed Specification: DS_GetDrives

### 📥 Inputs (Parameters)
- **$Site** (Type: Sharepoint.Site)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedSite** via Association from **$Site** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Call Microflow **Sharepoint.GetDrives** (Result: **$Result**)**
4. 🏁 **END:** Return `$Result`

**Final Result:** This process concludes by returning a [List] value.