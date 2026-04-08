# Microflow Detailed Specification: SUB_AllBidDownload_Create_B_NYY_D_NNY

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateB_NYY_D_NNY**)
      - Set **ecoATMCode** = `$AggregatedInventory/EcoId`
      - Set **Category** = `$AggregatedInventory/Category`
      - Set **DeviceId** = `$AggregatedInventory/DeviceId`
      - Set **BrandName** = `$AggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$AggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNYQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNY** = `'$0.00'`**
                  2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`
               ➔ **If [true]:**
                  1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNY** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [false]:**
                  1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNYQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNY** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`
                     ➔ **If [true]:**
                        1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`
               ➔ **If [true]:**
                  1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNYQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNY** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`
                     ➔ **If [true]:**
                        1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`
   ➔ **If [false]:**
      1. **Update **$CreateB_NYY_D_NNY**
      - Set **B_NYY_D_NNYQuantityCap** = `empty`
      - Set **B_NYY_D_NNY** = `'$0.00'`**
      2. 🏁 **END:** Return `$CreateB_NYY_D_NNY`

**Final Result:** This process concludes by returning a [Object] value.