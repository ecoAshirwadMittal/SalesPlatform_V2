# Microflow Detailed Specification: ACT_CheckBuyerCodeSelectHelper

### 📥 Inputs (Parameters)
- **$BuyerList** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
   │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
   │ 2. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │    │ 1. **Retrieve related **BuyerCode_Helper_BuyerCode** via Association from **$IteratorBuyerCode** (Result: **$BuyerCode_HelperList**)**
   │    └─ **End Loop**
   └─ **End Loop**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.