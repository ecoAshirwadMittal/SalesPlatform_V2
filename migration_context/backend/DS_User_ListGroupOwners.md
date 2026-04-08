# Microflow Detailed Specification: DS_User_ListGroupOwners

### 📥 Inputs (Parameters)
- **$Group** (Type: MicrosoftGraph.Group)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActive** (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_User_ListGroupOwners** (Result: **$UserList**)**
3. 🏁 **END:** Return `$UserList`

**Final Result:** This process concludes by returning a [List] value.