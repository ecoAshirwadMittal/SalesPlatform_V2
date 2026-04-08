# Microflow Detailed Specification: ACT_BidIMport_Cancel

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.BuyerCodeSelect_Helper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$ImportFile** (Type: Custom_Excel_Import.ImportFile)

### ⚙️ Execution Flow (Logic Steps)
1. **Delete**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.