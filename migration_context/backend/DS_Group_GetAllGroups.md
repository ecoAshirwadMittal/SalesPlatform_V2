# Microflow Detailed Specification: DS_Group_GetAllGroups

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Group_GetAll** (Result: **$GroupList**)**
2. 🏁 **END:** Return `$GroupList`

**Final Result:** This process concludes by returning a [List] value.