# Microflow Detailed Specification: VAL_Schedule_Auction

### 📥 Inputs (Parameters)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ValidationMessage** = `empty`**
2. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round1_End_DateTime > $SchedulingAuction_Helper/Round1_Start_DateTime`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round2_isActive = true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round2_End_DateTime > $SchedulingAuction_Helper/Round2_Start_DateTime`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction_Helper/isRound3Active = true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round3_End_Datetime > $SchedulingAuction_Helper/Round3_Start_DateTime`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$ValidationMessage`
                           ➔ **If [false]:**
                              1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 3 end date must be later than start date' else $ValidationMessage + ', Round 3 end date must be later than start date'`**
                              2. 🏁 **END:** Return `$ValidationMessage`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$ValidationMessage`
               ➔ **If [false]:**
                  1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 2 end date must be later than start date' else $ValidationMessage + ', Round 2 end date must be later than start date'`**
                  2. 🔀 **DECISION:** `$SchedulingAuction_Helper/isRound3Active = true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round3_End_Datetime > $SchedulingAuction_Helper/Round3_Start_DateTime`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$ValidationMessage`
                           ➔ **If [false]:**
                              1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 3 end date must be later than start date' else $ValidationMessage + ', Round 3 end date must be later than start date'`**
                              2. 🏁 **END:** Return `$ValidationMessage`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$ValidationMessage`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$SchedulingAuction_Helper/isRound3Active = true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round3_End_Datetime > $SchedulingAuction_Helper/Round3_Start_DateTime`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$ValidationMessage`
                     ➔ **If [false]:**
                        1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 3 end date must be later than start date' else $ValidationMessage + ', Round 3 end date must be later than start date'`**
                        2. 🏁 **END:** Return `$ValidationMessage`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `$ValidationMessage`
   ➔ **If [false]:**
      1. **Update Variable **$ValidationMessage** = `'Round 1 end date must be later than start date'`**
      2. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round2_isActive = true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round2_End_DateTime > $SchedulingAuction_Helper/Round2_Start_DateTime`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction_Helper/isRound3Active = true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round3_End_Datetime > $SchedulingAuction_Helper/Round3_Start_DateTime`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$ValidationMessage`
                           ➔ **If [false]:**
                              1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 3 end date must be later than start date' else $ValidationMessage + ', Round 3 end date must be later than start date'`**
                              2. 🏁 **END:** Return `$ValidationMessage`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$ValidationMessage`
               ➔ **If [false]:**
                  1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 2 end date must be later than start date' else $ValidationMessage + ', Round 2 end date must be later than start date'`**
                  2. 🔀 **DECISION:** `$SchedulingAuction_Helper/isRound3Active = true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round3_End_Datetime > $SchedulingAuction_Helper/Round3_Start_DateTime`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$ValidationMessage`
                           ➔ **If [false]:**
                              1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 3 end date must be later than start date' else $ValidationMessage + ', Round 3 end date must be later than start date'`**
                              2. 🏁 **END:** Return `$ValidationMessage`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$ValidationMessage`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$SchedulingAuction_Helper/isRound3Active = true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round3_End_Datetime > $SchedulingAuction_Helper/Round3_Start_DateTime`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$ValidationMessage`
                     ➔ **If [false]:**
                        1. **Update Variable **$ValidationMessage** = `if ($ValidationMessage = empty) then 'Round 3 end date must be later than start date' else $ValidationMessage + ', Round 3 end date must be later than start date'`**
                        2. 🏁 **END:** Return `$ValidationMessage`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `$ValidationMessage`

**Final Result:** This process concludes by returning a [String] value.