# Microflow Detailed Specification: SUB_DateTimeEmailBody

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeZoneOffset**)**
2. **Create Variable **$EndTimeEST** = `addHours($SchedulingAuction/End_DateTime,$TimeZoneOffset)`**
3. **Create Variable **$Day** = `parseInteger(formatDateTime($EndTimeEST, 'd'))`**
4. 🔀 **DECISION:** `$Day=11 or $Day=12 or $Day=13`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Day mod 10=1`
         ➔ **If [true]:**
            1. **Create Variable **$SuffixCase2** = `'st'`**
            2. **Create Variable **$DT2** = `formatDateTime($EndTimeEST, 'EEEE, MMMM d') + $SuffixCase2 + formatDateTime($EndTimeEST, ', yyyy hh:mm a') + ' EST'`**
            3. 🏁 **END:** Return `$DT2`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$Day mod 10=2`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$Day mod 10=3`
                     ➔ **If [true]:**
                        1. **Create Variable **$SuffixCase4** = `'rd'`**
                        2. **Create Variable **$DT4** = `formatDateTime($EndTimeEST, 'EEEE, MMMM d') + $SuffixCase4 + formatDateTime($EndTimeEST, ', yyyy hh:mm a') + ' EST'`**
                        3. 🏁 **END:** Return `$DT4`
                     ➔ **If [false]:**
                        1. **Create Variable **$SuffixCase1** = `'th'`**
                        2. **Create Variable **$DT1** = `formatDateTime($EndTimeEST, 'EEEE, MMMM d') + $SuffixCase1 + formatDateTime($EndTimeEST, ', yyyy hh:mm a') + ' EST'`**
                        3. 🏁 **END:** Return `$DT1`
               ➔ **If [true]:**
                  1. **Create Variable **$SuffixCase3** = `'nd'`**
                  2. **Create Variable **$DT3** = `formatDateTime($EndTimeEST, 'EEEE, MMMM d') + $SuffixCase3 + formatDateTime($EndTimeEST, ', yyyy hh:mm a') + ' EST'`**
                  3. 🏁 **END:** Return `$DT3`
   ➔ **If [true]:**
      1. **Create Variable **$SuffixCase1** = `'th'`**
      2. **Create Variable **$DT1** = `formatDateTime($EndTimeEST, 'EEEE, MMMM d') + $SuffixCase1 + formatDateTime($EndTimeEST, ', yyyy hh:mm a') + ' EST'`**
      3. 🏁 **END:** Return `$DT1`

**Final Result:** This process concludes by returning a [String] value.