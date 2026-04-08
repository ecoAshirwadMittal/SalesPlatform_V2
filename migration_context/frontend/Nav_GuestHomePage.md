# Microflow Detailed Specification: Nav_GuestHomePage

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DeepLink.DeepLinkHome** (Result: **$DeeplinkExecuted**)**
2. 🔀 **DECISION:** `$DeeplinkExecuted`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **ForgotPassword.LoginPage****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.