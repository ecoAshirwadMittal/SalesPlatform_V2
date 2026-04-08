# Microflow Detailed Specification: ACT_CreateListItem

### 📥 Inputs (Parameters)
- **$List** (Type: Sharepoint.List)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedList** via Association from **$List** (Result: **$Explorer**)**
2. **Create **Sharepoint.ListItem** (Result: **$NewListItem**)
      - Set **ListItem_Explorer** = `$Explorer`**
3. **Maps to Page: **Sharepoint.ListItem_Create****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.