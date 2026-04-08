# Nanoflow: ACT_Set_ShowLoginPassword

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Anonymous

## 📥 Inputs

- **$LoginCredentials** (EcoATM_UserManagement.LoginCredentials)

## ⚙️ Execution Flow

1. **Update **$LoginCredentials** (and Save to DB)
      - Set **Email** = `toLowerCase($LoginCredentials/Email)`**
2. **Call Microflow **AuctionUI.VAL_IsExistingAccount** (Result: **$IsExistingAccount**)**
3. 🔀 **DECISION:** `$IsExistingAccount =true`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `contains(toLowerCase($LoginCredentials/Email),'@ecoatm.com')`
         ➔ **If [false]:**
            1. **Update **$LoginCredentials**
      - Set **ShowPasssword** = `true`**
            2. **Call JS Action **WebActions.SetFocus** (Result: **$Variable**)**
            3. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call JS Action **NanoflowCommons.OpenURL** (Result: **$ReturnValueName**)**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
