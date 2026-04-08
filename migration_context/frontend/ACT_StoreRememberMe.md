# Nanoflow: ACT_StoreRememberMe

## 📥 Inputs

- **$LoginCredentials** (EcoATM_UserManagement.LoginCredentials)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$LoginCredentials/RememberMe = true`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.SetStorageItemObject** (Result: **$Variable**)**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call JS Action **NanoflowCommons.StorageItemExists** (Result: **$objectexists**)**
      2. 🔀 **DECISION:** `$objectexists = true`
         ➔ **If [true]:**
            1. **Call JS Action **NanoflowCommons.RemoveStorageItem** (Result: **$ReturnValueName**)**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
