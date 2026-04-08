# Microflow Detailed Specification: DS_User_MyProfile

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_User_GetMyProfile** (Result: **$User**)**
2. 🏁 **END:** Return `$User`

**Final Result:** This process concludes by returning a [Object] value.