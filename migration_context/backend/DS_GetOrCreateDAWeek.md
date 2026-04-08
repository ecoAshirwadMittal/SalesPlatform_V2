# Microflow Detailed Specification: DS_GetOrCreateDAWeek

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$DayofTheWeek** = `formatDateTime([%CurrentDateTime%],'EEEE')`**
2. **DB Retrieve **AuctionUI.Week** Filter: `[WeekEndDateTime>='[%CurrentDateTime%]']` (Result: **$CurrentWeek**)**
3. 🔀 **DECISION:** `$DayofTheWeek= 'Friday' or $DayofTheWeek= 'Saturday' or $DayofTheWeek= 'Sunday'`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_DA.DAWeek** Filter: `[EcoATM_DA.DAWeek_Week = $CurrentWeek]` (Result: **$DAWeek**)**
      2. 🔀 **DECISION:** `$DAWeek != empty`
         ➔ **If [true]:**
            1. **Update **$DAHelper**
      - Set **DAHelper_DAWeek** = `$DAWeek`**
            2. **Call Microflow **EcoATM_DA.ACT_LoadDAData****
            3. 🏁 **END:** Return `$DAWeek`
         ➔ **If [false]:**
            1. **Create **EcoATM_DA.DAWeek** (Result: **$NewDAWeek**)
      - Set **DAWeek_Week** = `$CurrentWeek`**
            2. **Update **$DAHelper**
      - Set **DAHelper_DAWeek** = `$NewDAWeek`**
            3. **Call Microflow **EcoATM_DA.ACT_LoadDAData****
            4. 🏁 **END:** Return `$NewDAWeek`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_DA.SUB_GetCurrentWeekMinusOne** (Result: **$LastWeek**)**
      2. **DB Retrieve **EcoATM_DA.DAWeek** Filter: `[EcoATM_DA.DAWeek_Week = $LastWeek]` (Result: **$DAWeek_LastWeek**)**
      3. 🔀 **DECISION:** `$DAWeek_LastWeek != empty`
         ➔ **If [true]:**
            1. **Update **$DAHelper**
      - Set **DAHelper_DAWeek** = `$DAWeek_LastWeek`**
            2. **Call Microflow **EcoATM_DA.ACT_LoadDAData****
            3. 🏁 **END:** Return `$DAWeek_LastWeek`
         ➔ **If [false]:**
            1. **Create **EcoATM_DA.DAWeek** (Result: **$NewDAWeek_LastWeek**)
      - Set **DAWeek_Week** = `$LastWeek`**
            2. **Update **$DAHelper**
      - Set **DAHelper_DAWeek** = `$NewDAWeek_LastWeek`**
            3. **Call Microflow **EcoATM_DA.ACT_LoadDAData****
            4. 🏁 **END:** Return `$NewDAWeek_LastWeek`

**Final Result:** This process concludes by returning a [Object] value.