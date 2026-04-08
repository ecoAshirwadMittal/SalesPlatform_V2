# Nanoflow: Nanoflow

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

## 📥 Inputs

- **$DG2_UiHelper** (EcoATM_BuyerManagement.DG2_UiHelper)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidRound** (AuctionUI.BidRound)

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$AuctionsFeature**)**
2. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ShowProgress**)**
3. 🔀 **DECISION:** `$AuctionsFeature/LegacyBidDataCreation`
   ➔ **If [false]:**
      1. **Call Microflow **AuctionUI.ACT_CreateBidDataOptimized** (Result: **$BidDataList**)**
      2. **Update **$DG2_UiHelper**
      - Set **IsBidDataLoaded** = `true`**
      3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      4. 🏁 **END:** Return `$DG2_UiHelper`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_CreateBidData** (Result: **$BidDataList**)**
      2. **Update **$DG2_UiHelper**
      - Set **IsBidDataLoaded** = `true`**
      3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      4. 🏁 **END:** Return `$DG2_UiHelper`

## 🏁 Returns
`Object`
