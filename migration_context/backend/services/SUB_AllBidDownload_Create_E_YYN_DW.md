# Microflow Detailed Specification: SUB_AllBidDownload_Create_E_YYN_DW

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateE_YYN_DW**)
      - Set **ecoATMCode** = `$AggregatedInventory/EcoId`
      - Set **Category** = `$AggregatedInventory/Category`
      - Set **DeviceId** = `$AggregatedInventory/DeviceId`
      - Set **BrandName** = `$AggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$AggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **MAXofGradeE_YYN** = `'$'+toString($AggregatedInventory/DWAvgTargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$AggregatedInventory/DWTotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$CreateE_YYN_DW**
      - Set **E_YYNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$CreateE_YYN_DW**
      - Set **E_YYN** = `'$0.00'`**
                  2. 🏁 **END:** Return `$CreateE_YYN_DW`
               ➔ **If [true]:**
                  1. **Update **$CreateE_YYN_DW**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return `$CreateE_YYN_DW`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$CreateE_YYN_DW**
      - Set **E_YYNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateE_YYN_DW**
      - Set **E_YYN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateE_YYN_DW`
                     ➔ **If [true]:**
                        1. **Update **$CreateE_YYN_DW**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateE_YYN_DW`
               ➔ **If [false]:**
                  1. **Update **$CreateE_YYN_DW**
      - Set **E_YYNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateE_YYN_DW**
      - Set **E_YYN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateE_YYN_DW`
                     ➔ **If [true]:**
                        1. **Update **$CreateE_YYN_DW**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateE_YYN_DW`
   ➔ **If [false]:**
      1. **Update **$CreateE_YYN_DW**
      - Set **E_YYNQuantityCap** = `empty`
      - Set **E_YYN** = `'$0.00'`**
      2. 🏁 **END:** Return `$CreateE_YYN_DW`

**Final Result:** This process concludes by returning a [Object] value.