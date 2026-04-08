# Microflow Detailed Specification: SUB_LoadPWSInvetory_UpdateBrandProperty

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **JavaCallAction**
3. 🔀 **DECISION:** `$NewBrandList!=empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. 🔄 **LOOP:** For each **$IteratorBrand** in **$NewBrandList**
         │ 1. **Create **EcoATM_PWSMDM.Brand** (Result: **$NewBrand**)
      - Set **Brand** = `$IteratorBrand/Value`**
         │ 2. **Call Microflow **Custom_Logging.SUB_Log_Info****
         │ 3. **Add **$$NewBrand
** to/from list **$BrandList****
         └─ **End Loop**
      3. **Commit/Save **$BrandList** to Database**
      4. **Call Microflow **Custom_Logging.SUB_Log_Info****
      5. **JavaCallAction**
      6. **Call Microflow **Custom_Logging.SUB_Log_Info****
      7. **JavaCallAction**
      8. **Call Microflow **Custom_Logging.SUB_Log_Info****
      9. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **JavaCallAction**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. **JavaCallAction**
      5. **Call Microflow **Custom_Logging.SUB_Log_Info****
      6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.