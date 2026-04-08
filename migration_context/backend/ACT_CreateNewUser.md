# Microflow Detailed Specification: ACT_CreateNewUser

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_UserManagement.EcoATMDirectUser** (Result: **$NewEcoATMDirectUser**)
      - Set **LandingPagePreference** = `EcoATM_UserManagement.ENUM_LandingPagePreference.Wholesale_Auction`**
2. **Create **EcoATM_UserManagement.NewUser_Helper** (Result: **$NewNewUser_Helper**)
      - Set **Entity_EcoATMDirectUser** = `$NewEcoATMDirectUser`**
3. **Maps to Page: **AuctionUI.EcoATMDirectUser_New****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.