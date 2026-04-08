# Microflow Detailed Specification: DS_User_DirectReports

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_User_ListDirectReports** (Result: **$UserList**)**
2. 🏁 **END:** Return `$UserList`

**Final Result:** This process concludes by returning a [List] value.