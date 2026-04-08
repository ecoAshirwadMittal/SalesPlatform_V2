# Nanoflow: OKP_CheckEmail

**Allowed Roles:** AuctionUI.Administrator

## 📥 Inputs

- **$EcoATMDirectUser** (EcoATM_UserManagement.EcoATMDirectUser)
- **$NewUser_Helper** (EcoATM_UserManagement.NewUser_Helper)

## ⚙️ Execution Flow

1. **LogMessage (Info)**
2. 🔀 **DECISION:** `contains(toLowerCase($EcoATMDirectUser/Email),'@ecoatm.com')`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `endsWith(toLowerCase($EcoATMDirectUser/Email),'.com')`
         ➔ **If [false]:**
            1. **Update **$NewUser_Helper** (and Save to DB)
      - Set **IsEcoAtmEmail** = `false`
      - Set **IsExternalUser** = `false`**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update **$NewUser_Helper** (and Save to DB)
      - Set **IsEcoAtmEmail** = `false`
      - Set **IsExternalUser** = `true`**
            2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Update **$NewUser_Helper** (and Save to DB)
      - Set **IsEcoAtmEmail** = `true`
      - Set **IsExternalUser** = `false`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
