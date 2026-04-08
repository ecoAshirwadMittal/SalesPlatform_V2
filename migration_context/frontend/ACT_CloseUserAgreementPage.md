# Nanoflow: ACT_CloseUserAgreementPage

## 📥 Inputs

- **$EcoATMDirectUser** (EcoATM_UserManagement.EcoATMDirectUser)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$EcoATMDirectUser/Acknowledgement = true`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.NAV_DashboardNavigationLogic_Bidder****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call JS Action **NanoflowCommons.SignOut** (Result: **$ReturnValueName**)**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
