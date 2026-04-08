# Nanoflow: ACT_OpenBidderDashboard_BuyerCodeSearch_2

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesRep

## 📥 Inputs

- **$BuyerCodeSelectSearch_Helper** (EcoATM_BuyerManagement.BuyerCodeSelectSearchHelper)

## ⚙️ Execution Flow

1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCodeSelectSearch_Helper** via Association from **$BuyerCodeSelectSearch_Helper** (Result: **$NP_BuyerCodeSelect_Helper**)**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$Parent_NPBuyerCodeSelectHelper**)**
3. **Call Nanoflow **AuctionUI.ACT_NavigateToBidderDashboard****
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
