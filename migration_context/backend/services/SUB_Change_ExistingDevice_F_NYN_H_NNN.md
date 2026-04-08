# Microflow Detailed Specification: SUB_Change_ExistingDevice_F_NYN_H_NNN

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)
- **$AllBidDownload** (Type: AuctionUI.AllBidDownload)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$AllBidDownload**
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNN** = `'$0.00'`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNN** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNN** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AllBidDownload**
      - Set **F_NYN_H_NNNQuantityCap** = `empty`
      - Set **F_NYN_H_NNN** = `'$0.00'`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.