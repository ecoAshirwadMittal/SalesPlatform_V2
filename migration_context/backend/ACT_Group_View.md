# Microflow Detailed Specification: ACT_Group_View

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$Group** (Type: MicrosoftGraph.Group)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Group_GetById** (Result: **$Group_Retrieved**)**
2. **Maps to Page: **MicrosoftGraph.Group_View****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.