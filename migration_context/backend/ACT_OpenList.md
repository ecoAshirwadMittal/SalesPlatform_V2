# Microflow Detailed Specification: ACT_OpenList

### 📥 Inputs (Parameters)
- **$List** (Type: Sharepoint.List)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **List_Explorer** via Association from **$List** (Result: **$Explorer**)**
2. **Update **$Explorer**
      - Set **SelectedList** = `$List`**
3. **Maps to Page: **Sharepoint.List_Details****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.