# Microflow Detailed Specification: SUB_AllBidDownload_Create_F_NYN_H_NNN_DW

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.AllBidDownload** (Result: **$Create_F_NYN_H_NNN_DW**)
      - Set **ecoATMCode** = `$AggregatedInventory/EcoId`
      - Set **Category** = `$AggregatedInventory/Category`
      - Set **DeviceId** = `$AggregatedInventory/DeviceId`
      - Set **BrandName** = `$AggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$AggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($AggregatedInventory/DWAvgTargetPrice)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$AggregatedInventory/DWTotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNN** = `'$0.00'`**
                  2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`
               ➔ **If [true]:**
                  1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`
                     ➔ **If [true]:**
                        1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`
               ➔ **If [false]:**
                  1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`
                     ➔ **If [true]:**
                        1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`
   ➔ **If [false]:**
      1. **Update **$Create_F_NYN_H_NNN_DW**
      - Set **F_NYN_H_NNNQuantityCap** = `empty`
      - Set **F_NYN_H_NNN** = `'$0.00'`**
      2. 🏁 **END:** Return `$Create_F_NYN_H_NNN_DW`

**Final Result:** This process concludes by returning a [Object] value.