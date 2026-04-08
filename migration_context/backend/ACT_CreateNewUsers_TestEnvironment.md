# Microflow Detailed Specification: ACT_CreateNewUsers_TestEnvironment

### 📥 Inputs (Parameters)
- **$EcoATMDirectUserList** (Type: EcoATM_UserManagement.EcoATMDirectUser)
- **$BuyerList** (Type: EcoATM_BuyerManagement.Buyer)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Email** (Type: Variable)
- **$UserRole** (Type: System.UserRole)
- **$BuyerName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Create **EcoATM_UserManagement.EcoATMDirectUser** (Result: **$NewEcoATMDirectUser**)
      - Set **Password** = `$HashedPassword`**
3. **Create **EcoATM_BuyerManagement.Buyer** (Result: **$NewBuyer**)
      - Set **EcoATMDirectUser_Buyer** = `$NewEcoATMDirectUser`**
4. **Create **EcoATM_BuyerManagement.BuyerCode** (Result: **$NewBuyerCode**)
      - Set **BuyerCode_Buyer** = `$NewBuyer`**
5. **Add **$$EcoATMDirectUserList** to/from list **$EcoATMDirectUserList****
6. **Add **$$NewBuyer** to/from list **$BuyerList****
7. **Add **$$NewBuyerCode** to/from list **$BuyerCode****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.