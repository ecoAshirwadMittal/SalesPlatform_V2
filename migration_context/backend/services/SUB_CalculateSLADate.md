# Microflow Detailed Specification: SUB_CalculateSLADate

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_PWS.PWSConstants**  (Result: **$PWSFeatureConfig**)**
3. **DB Retrieve **EcoATM_MDM.CompanyHoliday**  (Result: **$CompanyHolidayList**)**
4. **Create Variable **$ResultDate** = `[%CurrentDateTime%]`**
5. **Create Variable **$DaysInBetween** = `0`**
6. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **Update Variable **$ResultDate** = `addDays($ResultDate, -1)`**
   │ 2. **List Operation: **Find** on **$undefined** where `$ResultDate` (Result: **$isCompanyHoliday**)**
   │ 3. 🔀 **DECISION:** `formatDateTime($ResultDate,'u')!='6' and formatDateTime($ResultDate,'u')!='7' and $isCompanyHoliday=empty`
   │    ➔ **If [true]:**
   │       1. **Update Variable **$DaysInBetween** = `$DaysInBetween+1`**
   │    ➔ **If [false]:**
   └─ **End Loop**
7. **Call Microflow **Custom_Logging.SUB_Log_Info****
8. 🏁 **END:** Return `$ResultDate`

**Final Result:** This process concludes by returning a [DateTime] value.