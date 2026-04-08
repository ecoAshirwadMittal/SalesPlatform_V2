# Microflow Detailed Specification: SUB_SendCounterOfferReminderEmail

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.PWSConstants**  (Result: **$PWSConstants**)**
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus = 'Buyer_Acceptance'] [FirstReminderSent=false or SecondReminderSent=false]` (Result: **$OfferList**)**
3. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Create Variable **$HoursSinceSalesReview** = `hoursBetween($IteratorOffer/SalesReviewCompletedOn,[%CurrentDateTime%])`**
   │ 2. **Call Microflow **Custom_Logging.SUB_Log_Info****
   │ 3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$HoursSinceSalesReview>=$PWSConstants/HoursSecondCounterReminderEmail and $IteratorOffer/SecondReminderSent = false`
   │          ➔ **If [true]:**
   │             1. **Call Microflow **EcoATM_PWS.SUB_SendSecondReminderEmail****
   │             2. **Update **$IteratorOffer**
      - Set **SecondReminderSent** = `true`**
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
   │                ➔ **If [true]:**
   │                   1. 🔀 **DECISION:** `if $PWSConstants/HoursSecondCounterReminderEmail!=empty then ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $HoursSinceSalesReview<$PWSConstants/HoursSecondCounterReminderEmail and $IteratorOffer/FirstReminderSent= false) else ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $IteratorOffer/FirstReminderSent= false)`
   │                      ➔ **If [false]:**
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **EcoATM_PWS.SUB_SendFirstReminderEmail****
   │                         2. **Update **$IteratorOffer**
      - Set **FirstReminderSent** = `true`**
   │                ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
   │          ➔ **If [true]:**
   │             1. 🔀 **DECISION:** `if $PWSConstants/HoursSecondCounterReminderEmail!=empty then ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $HoursSinceSalesReview<$PWSConstants/HoursSecondCounterReminderEmail and $IteratorOffer/FirstReminderSent= false) else ($HoursSinceSalesReview>=$PWSConstants/HoursFirstCounterReminderEmail and $IteratorOffer/FirstReminderSent= false)`
   │                ➔ **If [false]:**
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **EcoATM_PWS.SUB_SendFirstReminderEmail****
   │                   2. **Update **$IteratorOffer**
      - Set **FirstReminderSent** = `true`**
   │          ➔ **If [false]:**
   └─ **End Loop**
4. **Commit/Save **$OfferList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.