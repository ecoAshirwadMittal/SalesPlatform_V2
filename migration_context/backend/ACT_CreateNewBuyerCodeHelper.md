# Microflow Detailed Specification: ACT_CreateNewBuyerCodeHelper

### 📥 Inputs (Parameters)
- **$BuyerCode_Helper** (Type: EcoATM_BuyerManagement.BuyerCode_Helper)
- **$NewBuyerHelper** (Type: EcoATM_BuyerManagement.NewBuyerHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$NewBuyerHelper** (and Save to DB)
      - Set **Code_DW** = `toUpperCase($NewBuyerHelper/Code_DW)`
      - Set **Code_PO** = `toUpperCase($NewBuyerHelper/Code_PO)`
      - Set **Code_WH** = `toUpperCase($NewBuyerHelper/Code_WH)`**
2. **Create Variable **$NewBuyerCodeValue** = `''`**
3. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   ➔ **If [true]:**
      1. **Update Variable **$NewBuyerCodeValue** = `$NewBuyerHelper/Code_DW`**
      2. **Call Microflow **AuctionUI.VAL_ValidateBuyerCode_PreSave** (Result: **$isValid**)**
      3. 🔀 **DECISION:** `$isValid`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
               ➔ **If [true]:**
                  1. **Update **$NewBuyerHelper**
      - Set **Code_DW** = `empty`**
                  2. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Purchasing_Order`
                     ➔ **If [true]:**
                        1. **Update **$NewBuyerHelper**
      - Set **Code_PO** = `empty`**
                        2. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
                           ➔ **If [true]:**
                              1. **Update **$NewBuyerHelper**
      - Set **Code_WH** = `empty`**
                              2. 🏁 **END:** Return `true`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Purchasing_Order`
         ➔ **If [true]:**
            1. **Update Variable **$NewBuyerCodeValue** = `$NewBuyerHelper/Code_PO`**
            2. **Call Microflow **AuctionUI.VAL_ValidateBuyerCode_PreSave** (Result: **$isValid**)**
            3. 🔀 **DECISION:** `$isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
                     ➔ **If [true]:**
                        1. **Update **$NewBuyerHelper**
      - Set **Code_DW** = `empty`**
                        2. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Purchasing_Order`
                           ➔ **If [true]:**
                              1. **Update **$NewBuyerHelper**
      - Set **Code_PO** = `empty`**
                              2. 🏁 **END:** Return `true`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
                                 ➔ **If [true]:**
                                    1. **Update **$NewBuyerHelper**
      - Set **Code_WH** = `empty`**
                                    2. 🏁 **END:** Return `true`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
               ➔ **If [true]:**
                  1. **Update Variable **$NewBuyerCodeValue** = `$NewBuyerHelper/Code_WH`**
                  2. **Call Microflow **AuctionUI.VAL_ValidateBuyerCode_PreSave** (Result: **$isValid**)**
                  3. 🔀 **DECISION:** `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [true]:**
                              1. **Update **$NewBuyerHelper**
      - Set **Code_DW** = `empty`**
                              2. 🏁 **END:** Return `true`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Purchasing_Order`
                                 ➔ **If [true]:**
                                    1. **Update **$NewBuyerHelper**
      - Set **Code_PO** = `empty`**
                                    2. 🏁 **END:** Return `true`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
                                       ➔ **If [true]:**
                                          1. **Update **$NewBuyerHelper**
      - Set **Code_WH** = `empty`**
                                          2. 🏁 **END:** Return `true`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Call Microflow **AuctionUI.VAL_ValidateBuyerCode_PreSave** (Result: **$isValid**)**
                  2. 🔀 **DECISION:** `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [true]:**
                              1. **Update **$NewBuyerHelper**
      - Set **Code_DW** = `empty`**
                              2. 🏁 **END:** Return `true`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Purchasing_Order`
                                 ➔ **If [true]:**
                                    1. **Update **$NewBuyerHelper**
      - Set **Code_PO** = `empty`**
                                    2. 🏁 **END:** Return `true`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BuyerCode_Helper/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
                                       ➔ **If [true]:**
                                          1. **Update **$NewBuyerHelper**
      - Set **Code_WH** = `empty`**
                                          2. 🏁 **END:** Return `true`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.