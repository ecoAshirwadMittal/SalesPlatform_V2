# Microflow Detailed Specification: ACT_LoadScheduleAuction_Helper

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
3. 🔀 **DECISION:** `$SchedulingAuctionList!=empty`
   ➔ **If [false]:**
      1. **Call Microflow **AuctionUI.ACT_Create_SchedulingAuction_Helper_Default** (Result: **$NewSchedulingAuction_Helper_Auction_Exists**)**
      2. 🏁 **END:** Return `$NewSchedulingAuction_Helper_Auction_Exists`
   ➔ **If [true]:**
      1. **List Operation: **Find** on **$undefined** where `1` (Result: **$SchedulingAuction_Round1**)**
      2. **List Operation: **Find** on **$undefined** where `2` (Result: **$SchedulingAuction_Round2**)**
      3. **List Operation: **Find** on **$undefined** where `3` (Result: **$SchedulingAuction_Round3**)**
      4. **Update **$SchedulingAuction_Helper**
      - Set **SchedulingAuction_Helper_Auction** = `$Auction`**
      5. 🔀 **DECISION:** `$SchedulingAuction_Round1 != empty`
         ➔ **If [true]:**
            1. **Update **$SchedulingAuction_Helper**
      - Set **Round1_Start_DateTime** = `$SchedulingAuction_Round1/Start_DateTime`
      - Set **Round1_End_DateTime** = `$SchedulingAuction_Round1/End_DateTime`
      - Set **Round1_Status** = `$SchedulingAuction_Round1/RoundStatus`**
            2. 🔀 **DECISION:** `$SchedulingAuction_Round2 != empty`
               ➔ **If [true]:**
                  1. **Update **$SchedulingAuction_Helper**
      - Set **Round2_Start_DateTime** = `$SchedulingAuction_Round2/Start_DateTime`
      - Set **Round2_End_DateTime** = `$SchedulingAuction_Round2/End_DateTime`
      - Set **isRound2Active** = `$SchedulingAuction_Round2/RoundStatus`
      - Set **Round2_isActive** = `$SchedulingAuction_Round2/HasRound`**
                  2. 🔀 **DECISION:** `$SchedulingAuction_Round3 != empty`
                     ➔ **If [true]:**
                        1. **Update **$SchedulingAuction_Helper**
      - Set **Round3_Start_DateTime** = `$SchedulingAuction_Round3/Start_DateTime`
      - Set **Round3_End_Datetime** = `$SchedulingAuction_Round3/End_DateTime`
      - Set **Round3_Status** = `$SchedulingAuction_Round3/RoundStatus`
      - Set **isRound3Active** = `$SchedulingAuction_Round3/HasRound`**
                        2. **Call Microflow **AuctionUI.ACT_LoadBuyerTotals****
                        3. 🏁 **END:** Return `$SchedulingAuction_Helper`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$SchedulingAuction_Helper`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `$SchedulingAuction_Helper`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `$SchedulingAuction_Helper`

**Final Result:** This process concludes by returning a [Object] value.