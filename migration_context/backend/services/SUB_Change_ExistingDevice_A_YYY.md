# Microflow Detailed Specification: SUB_Change_ExistingDevice_A_YYY

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)
- **$AllBidDownload** (Type: AuctionUI.AllBidDownload)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$AllBidDownload**
      - Set **MAXofGradeA_YYY** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **A_YYYEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$AllBidDownload**
      - Set **A_YYYQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **A_YYY** = `'$0.00'`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **A_YYY** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **A_YYYQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **A_YYY** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **A_YYY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **A_YYYQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **A_YYY** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **A_YYY** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AllBidDownload**
      - Set **A_YYYQuantityCap** = `empty`
      - Set **A_YYY** = `'$0.00'`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.