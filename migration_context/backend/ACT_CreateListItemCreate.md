# Microflow Detailed Specification: ACT_CreateListItemCreate

### 📥 Inputs (Parameters)
- **$ListItem** (Type: Sharepoint.ListItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ListItem_Explorer** via Association from **$ListItem** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Retrieve related **SelectedSite** via Association from **$Explorer** (Result: **$Site**)**
4. **Retrieve related **SelectedList** via Association from **$Explorer** (Result: **$List**)**
5. **Retrieve related **Fields** via Association from **$ListItem** (Result: **$Fields**)**
6. **Call Microflow **Sharepoint.CreateListItem** (Result: **$NewId**)**
7. **Show Message (Information): `Item was added as {1}.`**
8. **Close current page/popup**
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.