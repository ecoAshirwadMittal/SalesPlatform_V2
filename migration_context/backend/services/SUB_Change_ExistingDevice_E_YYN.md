# Microflow Detailed Specification: SUB_Change_ExistingDevice_E_YYN

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (Type: AuctionUI.BidData)
- **$AllBidDownload** (Type: AuctionUI.AllBidDownload)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$AllBidDownload**
      - Set **MAXofGradeE_YYN** = `'$'+toString($AggregatedInventory/AvgTargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$AggregatedInventory/TotalQuantity`**
2. 🔀 **DECISION:** `$BidData != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidData/BidQuantity != empty`
         ➔ **If [false]:**
            1. **Update **$AllBidDownload**
      - Set **E_YYNQuantityCap** = `empty`**
            2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **E_YYN** = `'$0.00'`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidData/BidQuantity > 0`
               ➔ **If [true]:**
                  1. **Update **$AllBidDownload**
      - Set **E_YYNQuantityCap** = `$BidData/BidQuantity`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **E_YYN** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$AllBidDownload**
      - Set **E_YYNQuantityCap** = `empty`**
                  2. 🔀 **DECISION:** `$BidData/BidAmount != empty`
                     ➔ **If [false]:**
                        1. **Update **$AllBidDownload**
      - Set **E_YYN** = `'$0.00'`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$AllBidDownload**
      - Set **E_YYN** = `'$'+toString($BidData/BidAmount)`**
                        2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AllBidDownload**
      - Set **E_YYNQuantityCap** = `empty`
      - Set **E_YYN** = `'$0.00'`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.