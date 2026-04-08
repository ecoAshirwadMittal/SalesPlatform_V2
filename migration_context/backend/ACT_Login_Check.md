# Microflow Detailed Specification: ACT_Login_Check

### 📥 Inputs (Parameters)
- **$LoginCredentials** (Type: AuctionUI.LoginCredentials)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$LoginSuccess = true`
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.Auctions_UI_Overview****
      2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.