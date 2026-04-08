# Microflow Detailed Specification: ACT_OrderStatus_Delete

### 📥 Inputs (Parameters)
- **$OrderStatus** (Type: EcoATM_PWS.OrderStatus)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus=$OrderStatus]` (Result: **$OfferList**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$Count=0`
   ➔ **If [true]:**
      1. **Delete**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. **Show Message (Information): `Order status has been successfully deleted.`**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. **Show Message (Warning): `Order Status is used by [{1}] order(s). It can not be deleted.`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.