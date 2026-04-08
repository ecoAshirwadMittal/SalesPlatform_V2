# Microflow Detailed Specification: ACT_Registration_Renew

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DocumentGeneration.SE_AccessToken_Refresh** (Result: **$IsSuccess**)**
2. 🔀 **DECISION:** `$IsSuccess = true`
   ➔ **If [true]:**
      1. **Show Message (Information): `Your app registration has been renewed successfully.`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Failed to renew your app registration. Please check your app logs for more details.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.