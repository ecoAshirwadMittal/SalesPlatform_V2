# Microflow Detailed Specification: ACT_CreateBuyerCodeHelper_DataWipe

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewBuyerCode_Helper**)
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Data_Wipe`**
2. 🏁 **END:** Return `$NewBuyerCode_Helper`

**Final Result:** This process concludes by returning a [Object] value.