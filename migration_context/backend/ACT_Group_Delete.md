# Microflow Detailed Specification: ACT_Group_Delete

### 📥 Inputs (Parameters)
- **$Group** (Type: MicrosoftGraph.Group)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActive** (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_Group_Delete** (Result: **$Deleted**)**
3. 🔀 **DECISION:** `$Deleted = true`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Delete**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.