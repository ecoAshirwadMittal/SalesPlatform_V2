# Microflow Detailed Specification: DS_User_ListGroupMembers

### 📥 Inputs (Parameters)
- **$Group** (Type: MicrosoftGraph.Group)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActive** (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_User_ListGroupMembers** (Result: **$UserList**)**
3. 🏁 **END:** Return `$UserList`

**Final Result:** This process concludes by returning a [List] value.