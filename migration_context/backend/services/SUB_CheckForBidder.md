# Microflow Detailed Specification: SUB_CheckForBidder

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **UserRoles** via Association from **$currentUser** (Result: **$UserRoleList**)**
2. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$Bidder**)**
3. 🔀 **DECISION:** `$Bidder!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.