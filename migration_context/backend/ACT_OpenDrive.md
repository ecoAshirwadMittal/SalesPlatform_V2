# Microflow Detailed Specification: ACT_OpenDrive

### 📥 Inputs (Parameters)
- **$Drive** (Type: Sharepoint.Drive)
- **$Site** (Type: Sharepoint.Site)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedSite** via Association from **$Site** (Result: **$Explorer**)**
2. **Update **$Explorer**
      - Set **SelectedDrive** = `$Drive`**
3. **Maps to Page: **Sharepoint.Drive_Details****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.