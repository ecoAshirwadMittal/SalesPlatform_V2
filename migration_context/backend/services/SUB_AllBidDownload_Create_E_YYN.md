# Microflow Detailed Specification: SUB_AllBidDownload_Create_E_YYN

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateE_YYN**)
      - Set **ecoATMCode** = `$AggregatedInventory/EcoId`
      - Set **Category** = `$AggregatedInventory/Category`
      - Set **DeviceId** = `$AggregatedInventory/DeviceId`
      - Set **BrandName** = `$AggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$AggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **MAXofGradeE_YYN** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$CreateE_YYN**
      - Set **E_YYNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$CreateE_YYN**
      - Set **E_YYN** = `'$0.00'`**
                  2. 🏁 **END:** Return `$CreateE_YYN`
               ➔ **If [true]:**
                  1. **Update **$CreateE_YYN**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return `$CreateE_YYN`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$CreateE_YYN**
      - Set **E_YYNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateE_YYN**
      - Set **E_YYN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateE_YYN`
                     ➔ **If [true]:**
                        1. **Update **$CreateE_YYN**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateE_YYN`
               ➔ **If [false]:**
                  1. **Update **$CreateE_YYN**
      - Set **E_YYNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$CreateE_YYN**
      - Set **E_YYN** = `'$0.00'`**
                        2. 🏁 **END:** Return `$CreateE_YYN`
                     ➔ **If [true]:**
                        1. **Update **$CreateE_YYN**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return `$CreateE_YYN`
   ➔ **If [false]:**
      1. **Update **$CreateE_YYN**
      - Set **E_YYNQuantityCap** = `empty`
      - Set **E_YYN** = `'$0.00'`**
      2. 🏁 **END:** Return `$CreateE_YYN`

**Final Result:** This process concludes by returning a [Object] value.