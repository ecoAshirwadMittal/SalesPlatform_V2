# Microflow Detailed Specification: SUB_Change_ExistingDevice_C_YNY_G_YNN

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)
- **$AllBidDownload** (Type: AuctionUI.AllBidDownload)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$AllBidDownload**
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNN** = `'$0.00'`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNN** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNN** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AllBidDownload**
      - Set **C_YNY_G_YNNQuantityCap** = `empty`
      - Set **C_YNY_G_YNN** = `'$0.00'`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.