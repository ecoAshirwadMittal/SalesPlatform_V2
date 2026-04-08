# Microflow Detailed Specification: DS_CreateDataDogTest

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.MF_GetSecurityRoles** (Result: **$Variable**)**
2. **Create **AuctionUI.DataDogTest** (Result: **$NewDataDogTest**)
      - Set **RoleList** = `$Variable`**
3. 🏁 **END:** Return `$NewDataDogTest`

**Final Result:** This process concludes by returning a [Object] value.