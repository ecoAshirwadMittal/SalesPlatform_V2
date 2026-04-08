# Microflow Detailed Specification: ACT_Offers_UpdateOfferStatusSnowflake

### 📥 Inputs (Parameters)
- **$UpdateSnowflakeHelper** (Type: EcoATM_PWS.UpdateSnowflakeHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SnowflakeOfferStatusUpdateAdmin'`**
2. **Create Variable **$Description** = `'Snowflake Offer Status Update By Admin: Start date: '+toString($UpdateSnowflakeHelper/FromDate)+'End Date: '+toString($UpdateSnowflakeHelper/ToDate)`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$FromDate** = `trimToDays($UpdateSnowflakeHelper/FromDate)`**
5. **Create Variable **$ToDate** = `trimToDays(addDays($UpdateSnowflakeHelper/ToDate,1))`**
6. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferSubmissionDate>= $FromDate and OfferSubmissionDate<=$ToDate]` (Result: **$OfferList**)**
7. 🔀 **DECISION:** `$OfferList!=empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
         │ 1. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
         └─ **End Loop**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. **Close current page/popup**
      4. **Show Message (Information): `Sending data to Snowflake has been initiated. It might take some time for the data to be sent.`**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Close current page/popup**
      2. **Show Message (Information): `There are no Offers/Orders available for update in Snowflake for the selected date range.`**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.