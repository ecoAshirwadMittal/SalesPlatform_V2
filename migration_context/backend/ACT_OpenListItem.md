# Microflow Detailed Specification: ACT_OpenListItem

### 📥 Inputs (Parameters)
- **$ListItem** (Type: Sharepoint.ListItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ListItem_Explorer** via Association from **$ListItem** (Result: **$Explorer**)**
2. **Retrieve related **SelectedSite** via Association from **$Explorer** (Result: **$Site**)**
3. **Retrieve related **SelectedList** via Association from **$Explorer** (Result: **$List**)**
4. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
5. **Call Microflow **Sharepoint.GetListItem** (Result: **$Item**)**
6. **Update **$Item**
      - Set **ListItem_Explorer** = `$Explorer`**
7. **Maps to Page: **Sharepoint.ListItem_Details****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.