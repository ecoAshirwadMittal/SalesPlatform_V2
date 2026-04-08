# Nanoflow: ACT_NavigateToBidderDashboard_WithTabId

**Allowed Roles:** AuctionUI.Bidder

## 📥 Inputs

- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

## ⚙️ Execution Flow

1. **Call Nanoflow **EcoATM_Direct_Theme.DS_GetBuyerCode_SessionAndTabHelper** (Result: **$BuyerCode_SessionAndTabHelper**)**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
3. **Update **$BuyerCode_SessionAndTabHelper** (and Save to DB)
      - Set **BuyerCode_SessionAndTabHelper_BuyerCode** = `$BuyerCode`**
4. **Call Microflow **EcoATM_PWS.ACT_NavigateToBidderDashboard****
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
