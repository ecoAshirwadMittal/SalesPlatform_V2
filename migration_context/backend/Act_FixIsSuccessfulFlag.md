# Microflow Detailed Specification: Act_FixIsSuccessfulFlag

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Order** Filter: `[OracleOrderStatus = 'Success']` (Result: **$OrderList**)**
2. 🔄 **LOOP:** For each **$IteratorOrder** in **$OrderList**
   │ 1. **Update **$IteratorOrder**
      - Set **IsSuccessful** = `true`**
   └─ **End Loop**
3. **Commit/Save **$OrderList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.