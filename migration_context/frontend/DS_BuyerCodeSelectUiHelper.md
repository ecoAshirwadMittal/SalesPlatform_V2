# Nanoflow: DS_BuyerCodeSelectUiHelper

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep

## 📥 Inputs

- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

## ⚙️ Execution Flow

1. **Retrieve related **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** via Association from **$Parent_NPBuyerCodeSelectHelper** (Result: **$NP_BuyerCodeSelect_HelperList**)**
2. **List Operation: **FindByExpression** on **$NP_BuyerCodeSelect_HelperList** where `$currentObject/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Premium_Wholesale` (Result: **$NP_BuyerCodeSelect_Helper_WholeSale**)**
3. **List Operation: **FindByExpression** on **$NP_BuyerCodeSelect_HelperList** where `$currentObject/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Premium_Wholesale` (Result: **$NP_BuyerCodeSelect_Helper_PWS**)**
4. **Create **AuctionUI.BuyerCodeSelectUiHelper** (Result: **$NewBuyerCodeUiHelper**)
      - Set **IsWholesaleUser** = `$NP_BuyerCodeSelect_Helper_WholeSale != empty`
      - Set **IsPWSUser** = `$NP_BuyerCodeSelect_Helper_PWS != empty`**
5. 🏁 **END:** Return `$NewBuyerCodeUiHelper`

## 🏁 Returns
`Object`
