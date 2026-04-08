# Nanoflow: ACT_GetLocalStoredLogin

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Anonymous

## 📥 Inputs

- **$LoginCredentials** (EcoATM_UserManagement.LoginCredentials)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.StorageItemExists** (Result: **$loginexists**)**
2. 🔀 **DECISION:** `$loginexists = true`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.GetStorageItemObject** (Result: **$LocalLoginCredentials**)**
      2. 🏁 **END:** Return `$LocalLoginCredentials`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$LoginCredentials`

## 🏁 Returns
`Object`
