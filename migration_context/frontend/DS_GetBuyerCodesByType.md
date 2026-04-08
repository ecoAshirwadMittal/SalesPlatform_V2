# Nanoflow: DS_GetBuyerCodesByType

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep

## 📥 Inputs

- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$IsPWS** (Boolean)

## ⚙️ Execution Flow

1. **Retrieve related **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** via Association from **$Parent_NPBuyerCodeSelectHelper** (Result: **$NP_BuyerCodeSelect_HelperList**)**
2. **List Operation: **FilterByExpression** on **$NP_BuyerCodeSelect_HelperList** where `if $IsPWS then $currentObject/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Premium_Wholesale else $currentObject/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Premium_Wholesale` (Result: **$NP_BuyerCodeSelect_HelperList_Filtered**)**
3. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList_Filtered`

## 🏁 Returns
`List`
