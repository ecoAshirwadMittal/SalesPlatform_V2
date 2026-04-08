# Microflow Detailed Specification: ACT_GetSalesRepBuyerCodes_PopUp

### 📥 Inputs (Parameters)
- **$BuyerCodeSelectSearch_Helper** (Type: EcoATM_BuyerManagement.BuyerCodeSelectSearchHelper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** via Association from **$Parent_NPBuyerCodeSelectHelper** (Result: **$NP_BuyerCodeSelect_HelperList**)**
2. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList`

**Final Result:** This process concludes by returning a [List] value.