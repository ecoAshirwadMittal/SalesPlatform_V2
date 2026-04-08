# Microflow Detailed Specification: ACT_Set_ShowLoginPassword_microflow

### 📥 Inputs (Parameters)
- **$LoginCredentials** (Type: AuctionUI.LoginCredentials)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `contains(toLowerCase($LoginCredentials/Email),'@ecoatm.com')`
   ➔ **If [false]:**
      1. **Update **$LoginCredentials**
      - Set **ShowPasssword** = `true`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.