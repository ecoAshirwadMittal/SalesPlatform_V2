# Nanoflow: ACT_Login_Client

**Allowed Roles:** AuctionUI.Anonymous, AuctionUI.Administrator

## 📥 Inputs

- **$LoginCredentials** (EcoATM_UserManagement.LoginCredentials)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$LoginCredentials/Password!= empty`
   ➔ **If [true]:**
      1. **Call Nanoflow **AuctionUI.ACT_StoreRememberMe****
      2. **Call JS Action **NanoflowCommons.SignIn** (Result: **$LoginResult**)**
      3. 🔀 **DECISION:** `$LoginResult!=401`
         ➔ **If [true]:**
            1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback on **Password**: `Incorrect password`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback on **Password**: `Enter your password`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
