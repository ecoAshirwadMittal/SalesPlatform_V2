# Microflow Detailed Specification: DS_GetListItems

### 📥 Inputs (Parameters)
- **$List** (Type: Sharepoint.List)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **List_Explorer** via Association from **$List** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Retrieve related **SelectedSite** via Association from **$Explorer** (Result: **$Site**)**
4. **Call Microflow **Sharepoint.GetListItems** (Result: **$Result**)**
5. 🔄 **LOOP:** For each **$IteratorListItem** in **$Result**
   │ 1. **Retrieve related **ListItemContentType** via Association from **$IteratorListItem** (Result: **$ContentType**)**
   │ 2. **Update **$IteratorListItem**
      - Set **ListItem_Explorer** = `$Explorer`
      - Set **ContentTypeDisplayName** = `if $ContentType != empty then $ContentType/Name else empty`**
   └─ **End Loop**
6. 🏁 **END:** Return `$Result`

**Final Result:** This process concludes by returning a [List] value.