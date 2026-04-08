# Microflow Detailed Specification: ACT_OrderStatus_ExportJSON

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_PWS.OrderStatus**  (Result: **$OrderStatusList**)**
3. 🔀 **DECISION:** `$OrderStatusList!=empty`
   ➔ **If [true]:**
      1. **Create **EcoATM_PWS.ManageFileDocument** (Result: **$NewManageFileDocument**)
      - Set **Name** = `'orderStatus.json'`
      - Set **DeleteAfterDownload** = `true`**
      2. **ExportXml**
      3. **DownloadFile**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. **Show Message (Information): `No order status defined.`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.