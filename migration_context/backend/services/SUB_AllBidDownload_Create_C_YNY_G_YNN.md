# Microflow Detailed Specification: SUB_AllBidDownload_Create_C_YNY_G_YNN

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateC_YNY_G_YNN**)
      - Set **ecoATMCode** = `$AggregatedInventory/EcoId`
      - Set **Category** = `$AggregatedInventory/Category`
      - Set **DeviceId** = `$AggregatedInventory/DeviceId`
      - Set **BrandName** = `$AggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$AggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNN** = `'$0.00'`**
                  2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`
               ➔ **If [true]:**
                  1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [false]:**
                  1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`
                     ➔ **If [true]:**
                        1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`
               ➔ **If [true]:**
                  1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`
                     ➔ **If [true]:**
                        1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`
   ➔ **If [false]:**
      1. **Update **$CreateC_YNY_G_YNN**
      - Set **C_YNY_G_YNNQuantityCap** = `empty`
      - Set **C_YNY_G_YNN** = `'$0.00'`**
      2. 🏁 **END:** Return `$CreateC_YNY_G_YNN`

**Final Result:** This process concludes by returning a [Object] value.