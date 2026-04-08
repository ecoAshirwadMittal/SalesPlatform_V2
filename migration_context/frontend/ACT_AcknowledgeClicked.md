# Nanoflow: ACT_AcknowledgeClicked

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.Compliance, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User

## 📥 Inputs

- **$EcoATMDirectUser** (EcoATM_UserManagement.EcoATMDirectUser)

## ⚙️ Execution Flow

1. **Commit/Save **$EcoATMDirectUser** to Database**
2. 🔀 **DECISION:** `$EcoATMDirectUser/Acknowledgement = true`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Call Microflow **AuctionUI.NAV_DashboardNavigationLogic_Bidder****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call JS Action **NanoflowCommons.SignOut** (Result: **$ReturnValueName**)**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
