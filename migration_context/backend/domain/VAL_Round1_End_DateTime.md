# Microflow Detailed Specification: VAL_Round1_End_DateTime

### 📥 Inputs (Parameters)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsValid** = `true`**
2. **Create Variable **$Round1_End_DateTimeValidationFeedback** = `''`**
3. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round1_End_DateTime = empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$SchedulingAuction_Helper/Round1_End_DateTime > $SchedulingAuction_Helper/Round2_Start_DateTime`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `trim($Round1_End_DateTimeValidationFeedback) = ''`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$IsValid`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return `$IsValid`
         ➔ **If [false]:**
            1. **Update Variable **$IsValid** = `false`**
            2. **Update Variable **$Round1_End_DateTimeValidationFeedback** = `if trim($Round1_End_DateTimeValidationFeedback) = '' then 'End date must be after Begin date' else $Round1_End_DateTimeValidationFeedback + ' ' + 'End date must be after Begin date'`**
            3. 🔀 **DECISION:** `trim($Round1_End_DateTimeValidationFeedback) = ''`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$IsValid`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return `$IsValid`
   ➔ **If [false]:**
      1. **Update Variable **$IsValid** = `false`**
      2. **Update Variable **$Round1_End_DateTimeValidationFeedback** = `$Round1_End_DateTimeValidationFeedback + 'End date must have value'`**
      3. 🔀 **DECISION:** `trim($Round1_End_DateTimeValidationFeedback) = ''`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$IsValid`
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return `$IsValid`

**Final Result:** This process concludes by returning a [Boolean] value.