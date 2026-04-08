# Microflow Detailed Specification: ACT_ShowUserEditPage

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EcoATMDirectUser/IsLocalUser`
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.EcoATMDirectUser_Edit_Bidder****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **AuctionUI.EcoATMDirectUser_Edit****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.