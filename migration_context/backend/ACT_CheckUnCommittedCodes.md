# Microflow Detailed Specification: ACT_CheckUnCommittedCodes

### 📥 Inputs (Parameters)
- **$NewBuyerHelper** (Type: EcoATM_BuyerManagement.NewBuyerHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$NewBuyerHelper/Code_DW = empty or trim($NewBuyerHelper/Code_DW) =''`
   ➔ **If [false]:**
      1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_DW**)
      - Set **Code** = `$NewBuyerHelper/Code_DW`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Data_Wipe`**
      2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$DW_isValid**)**
      3. 🔀 **DECISION:** `$DW_isValid`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$NewBuyerHelper/Code_PO = empty or trim($NewBuyerHelper/Code_PO) =''`
               ➔ **If [false]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_PO**)
      - Set **Code** = `$NewBuyerHelper/Code_PO`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Purchasing_Order`**
                  2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$PO_isValid**)**
                  3. 🔀 **DECISION:** `$PO_isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$NewBuyerHelper/Code_WH = empty or trim($NewBuyerHelper/Code_WH) =''`
                           ➔ **If [false]:**
                              1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_WH**)
      - Set **Code** = `$NewBuyerHelper/Code_WH`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Wholesale`**
                              2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$WH_isValid**)**
                              3. 🔀 **DECISION:** `$WH_isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `true`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `false`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$NewBuyerHelper/Code_WH = empty or trim($NewBuyerHelper/Code_WH) =''`
                     ➔ **If [false]:**
                        1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_WH**)
      - Set **Code** = `$NewBuyerHelper/Code_WH`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Wholesale`**
                        2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$WH_isValid**)**
                        3. 🔀 **DECISION:** `$WH_isValid`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `true`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `false`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$NewBuyerHelper/Code_PO = empty or trim($NewBuyerHelper/Code_PO) =''`
         ➔ **If [false]:**
            1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_PO**)
      - Set **Code** = `$NewBuyerHelper/Code_PO`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Purchasing_Order`**
            2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$PO_isValid**)**
            3. 🔀 **DECISION:** `$PO_isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$NewBuyerHelper/Code_WH = empty or trim($NewBuyerHelper/Code_WH) =''`
                     ➔ **If [false]:**
                        1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_WH**)
      - Set **Code** = `$NewBuyerHelper/Code_WH`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Wholesale`**
                        2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$WH_isValid**)**
                        3. 🔀 **DECISION:** `$WH_isValid`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `true`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `false`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$NewBuyerHelper/Code_WH = empty or trim($NewBuyerHelper/Code_WH) =''`
               ➔ **If [false]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCode_Helper** (Result: **$NewNewBuyerCodeHelper_WH**)
      - Set **Code** = `$NewBuyerHelper/Code_WH`
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Wholesale`**
                  2. **Call Microflow **AuctionUI.ACT_CreateNewBuyerCodeHelper** (Result: **$WH_isValid**)**
                  3. 🔀 **DECISION:** `$WH_isValid`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.