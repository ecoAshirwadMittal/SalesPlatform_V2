# Microflow Detailed Specification: DS_GetLists

### 📥 Inputs (Parameters)
- **$Site** (Type: Sharepoint.Site)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedSite** via Association from **$Site** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Call Microflow **Sharepoint.GetLists** (Result: **$Result**)**
4. 🔄 **LOOP:** For each **$IteratorList** in **$Result**
   │ 1. **Update **$IteratorList**
      - Set **List_Explorer** = `$Explorer`**
   └─ **End Loop**
5. 🏁 **END:** Return `$Result`

**Final Result:** This process concludes by returning a [List] value.