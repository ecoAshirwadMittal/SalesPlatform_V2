# Microflow Detailed Specification: SUB_Change_ExistingDevice_B_NYY_D_NNY

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)
- **$AllBidDownload** (Type: AuctionUI.AllBidDownload)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$AllBidDownload**
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNYQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNY** = `'$0.00'`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNY** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNYQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNY** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNYQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNY** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AllBidDownload**
      - Set **B_NYY_D_NNYQuantityCap** = `empty`
      - Set **B_NYY_D_NNY** = `'$0.00'`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.