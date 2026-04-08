# Nanoflow: OCH_ValidateAndSaveBidData

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

## 📥 Inputs

- **$BidData** (AuctionUI.BidData)

## ⚙️ Execution Flow

1. **Call JS Action **AuctionUI.RestrictToNumbers****
2. 🔀 **DECISION:** `$BidData/BidQuantity != empty and $BidData/BidQuantity < 0`
   ➔ **If [true]:**
      1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/LastValidBidQuantity`**
      2. 🔀 **DECISION:** `$BidData/BidAmount = empty`
         ➔ **If [true]:**
            1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`**
            2. 🔀 **DECISION:** `($BidData/BidAmount != empty and $BidData/BidAmount < 0)`
               ➔ **If [true]:**
                  1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/LastValidBidAmount`**
                  2. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `($BidData/BidAmount != empty and $BidData/BidAmount < 0)`
               ➔ **If [true]:**
                  1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/LastValidBidAmount`**
                  2. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$BidData/BidAmount = empty`
         ➔ **If [true]:**
            1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`**
            2. 🔀 **DECISION:** `($BidData/BidAmount != empty and $BidData/BidAmount < 0)`
               ➔ **If [true]:**
                  1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/LastValidBidAmount`**
                  2. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `($BidData/BidAmount != empty and $BidData/BidAmount < 0)`
               ➔ **If [true]:**
                  1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/LastValidBidAmount`**
                  2. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = 1 or ($BidData/PreviousRoundBidAmount = 0 and $BidData/PreviousRoundBidQuantity = empty)`
                     ➔ **If [true]:**
                        1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                        2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BidData/BidAmount = empty or ($BidData/PreviousRoundBidAmount != empty and $BidData/BidAmount < $BidData/PreviousRoundBidAmount)`
                           ➔ **If [true]:**
                              1. **Update **$BidData**
      - Set **BidAmount** = `$BidData/PreviousRoundBidAmount`
      - Set **LastValidBidAmount** = `$BidData/PreviousRoundBidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`**
                              2. 🔀 **DECISION:** `$BidData/BidQuantity = empty`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$BidData/PreviousRoundBidQuantity = empty`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$BidData/BidQuantity < $BidData/PreviousRoundBidQuantity`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **BidQuantity** = `$BidData/PreviousRoundBidQuantity`
      - Set **LastValidBidQuantity** = `$BidData/PreviousRoundBidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidAmount** = `$BidData/BidAmount`
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BidData/PreviousRoundBidAmount = empty`
                                             ➔ **If [true]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `$BidData/BidQuantity`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`
      - Set **BidQuantity** = `empty`**
                                                2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                                3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$BidData**
      - Set **LastValidBidQuantity** = `empty`**
                                    2. **Call Microflow **EcoATM_BuyerManagement.OCH_SaveBidData****
                                    3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
