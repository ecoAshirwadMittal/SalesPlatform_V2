# Nanoflow: ACT_BidData_SelectImportSheet

**Allowed Roles:** EcoATM_BidData.User

## 📥 Inputs

- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

## ⚙️ Execution Flow

1. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
2. **Create **AuctionUI.BidUploadPageHelper** (Result: **$NewBidUploadPageHelper**)
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus._New`
      - Set **AuctionTitle** = `$BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle`**
3. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
4. **Open Page: **EcoATM_BidData.PG_BidData_XMLUpload_BidRound****
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
