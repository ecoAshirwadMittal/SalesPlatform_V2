# Microflow Detailed Specification: SUB_AllBidDownload_Create_A_YYY_DW

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateA_YYY_DW**)
      - Set **ecoATMCode** = `$AggregatedInventory/EcoId`
      - Set **Category** = `$AggregatedInventory/Category`
      - Set **DeviceId** = `$AggregatedInventory/DeviceId`
      - Set **BrandName** = `$AggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$AggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **MAXofGradeA_YYY** = `'$'+toString($AggregatedInventory/DWAvgTargetPrice)`
      - Set **A_YYYEstimatedQuantity** = `$AggregatedInventory/DWTotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$CreateA_YYY_DW**
      - Set **A_YYYQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$CreateA_YYY_DW**
      - Set **A_YYY** = `'$0.00'`**
                  2. 🏁 **END:** Return `$CreateA_YYY_DW`
               ➔ **If [true]:**
                  1. **Update **$CreateA_YYY_DW**
      - Set **A_YYY** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return `$CreateA_YYY_DW`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$CreateA_YYY_DW**
      - Set **A_YYYQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateA_YYY_DW**
      - Set **A_YYY** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateA_YYY_DW`
                     ➔ **If [true]:**
                        1. **Update **$CreateA_YYY_DW**
      - Set **A_YYY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateA_YYY_DW`
               ➔ **If [false]:**
                  1. **Update **$CreateA_YYY_DW**
      - Set **A_YYYQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateA_YYY_DW**
      - Set **A_YYY** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateA_YYY_DW`
                     ➔ **If [true]:**
                        1. **Update **$CreateA_YYY_DW**
      - Set **A_YYY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateA_YYY_DW`
   ➔ **If [false]:**
      1. **Update **$CreateA_YYY_DW**
      - Set **A_YYYQuantityCap** = `empty`
      - Set **A_YYY** = `'$0.00'`**
      2. 🏁 **END:** Return `$CreateA_YYY_DW`

**Final Result:** This process concludes by returning a [Object] value.