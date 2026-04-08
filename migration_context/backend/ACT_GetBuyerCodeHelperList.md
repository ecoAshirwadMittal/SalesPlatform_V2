# Microflow Detailed Specification: ACT_GetBuyerCodeHelperList

### 📥 Inputs (Parameters)
- **$BuyerCode_Helper** (Type: EcoATM_BuyerManagement.BuyerCode_Helper)
- **$Buyer** (Type: EcoATM_BuyerManagement.NewBuyerHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NewBuyerCodeHelper_NewBuyerHelper** via Association from **$Buyer** (Result: **$NewBuyerCodeHelperList**)**
2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = $BuyerCode_Helper/BuyerCodeType` (Result: **$FilteredBuyerCodeHelperList**)**
3. 🏁 **END:** Return `$FilteredBuyerCodeHelperList`

**Final Result:** This process concludes by returning a [List] value.