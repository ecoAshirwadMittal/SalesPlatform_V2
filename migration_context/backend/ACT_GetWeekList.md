# Microflow Detailed Specification: ACT_GetWeekList

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$CurrentWeek**)**
2. **DB Retrieve **AuctionUI.Week**  (Result: **$WeekList**)**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorWeek** in **$WeekList**
   │ 1. **Create **AuctionUI.Week_Picker_Helper** (Result: **$NewWeek_Picker_Helper**)
      - Set **Week_Picker_Helper_Week** = `$IteratorWeek`
      - Set **IsCurrentWeek** = `if $CurrentWeek =$IteratorWeek then true else false`**
   │ 2. **Add **$$NewWeek_Picker_Helper** to/from list **$Week_Picker_HelperList****
   └─ **End Loop**
5. **Commit/Save **$Week_Picker_HelperList** to Database**
6. 🏁 **END:** Return `$Week_Picker_HelperList`

**Final Result:** This process concludes by returning a [List] value.