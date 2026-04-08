# Microflow Detailed Specification: ACT_SendCounterOfferReminderEmails

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.PWSConstants**  (Result: **$PWSConstants**)**
2. **Create Variable **$HoursSinceSalesReview** = `hoursBetween($Offer/SalesReviewCompletedOn,[%CurrentDateTime%])`**
3. **Call Microflow **Custom_Logging.SUB_Log_Info****
4. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$HoursSinceSalesReview>=$PWSConstants/HoursSecondCounterReminderEmail and $Offer/SecondReminderSent = false`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_SendSecondReminderEmail****
            2. **Update **$Offer** (and Save to DB)
      - Set **SecondReminderSent** = `true`**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `if $PWSConstants/HoursSecondCounterReminderEmail!=empty then ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $HoursSinceSalesReview<$PWSConstants/HoursSecondCounterReminderEmail and $Offer/FirstReminderSent= false) else ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $Offer/FirstReminderSent= false)`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_PWS.SUB_SendFirstReminderEmail****
                        2. **Update **$Offer** (and Save to DB)
      - Set **FirstReminderSent** = `true`**
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `if $PWSConstants/HoursSecondCounterReminderEmail!=empty then ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $HoursSinceSalesReview<$PWSConstants/HoursSecondCounterReminderEmail and $Offer/FirstReminderSent= false) else ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $Offer/FirstReminderSent= false)`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_SendFirstReminderEmail****
                  2. **Update **$Offer** (and Save to DB)
      - Set **FirstReminderSent** = `true`**
                  3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.