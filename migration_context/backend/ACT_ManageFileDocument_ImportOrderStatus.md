# Microflow Detailed Specification: ACT_ManageFileDocument_ImportOrderStatus

### 📥 Inputs (Parameters)
- **$ManageFileDocument** (Type: EcoATM_PWS.ManageFileDocument)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ManageFileDocument!=empty and $ManageFileDocument/HasContents`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. **JavaCallAction**
      3. **ImportXml**
      4. **DB Retrieve **EcoATM_PWS.OrderStatus**  (Result: **$ExistingOrderStatusList**)**
      5. **CreateList**
      6. 🔄 **LOOP:** For each **$IteratorOrderStatus** in **$NewOrderStatusList**
         │ 1. **List Operation: **Find** on **$undefined** where `$IteratorOrderStatus/SystemStatus` (Result: **$FoundOrderStatus**)**
         │ 2. 🔀 **DECISION:** `$FoundOrderStatus!=empty`
         │    ➔ **If [true]:**
         │       1. **Update **$FoundOrderStatus**
      - Set **InternalStatusText** = `$IteratorOrderStatus/InternalStatusText`
      - Set **ExternalStatusText** = `$IteratorOrderStatus/ExternalStatusText`
      - Set **InterStatusHexCode** = `$IteratorOrderStatus/InterStatusHexCode`
      - Set **ExternalStatusHexCode** = `$IteratorOrderStatus/ExternalStatusHexCode`
      - Set **SystemStatusDescription** = `$IteratorOrderStatus/SystemStatusDescription`**
         │       2. **Add **$$FoundOrderStatus
** to/from list **$FinalOrderStatusList****
         │    ➔ **If [false]:**
         │       1. **Create **EcoATM_PWS.OrderStatus** (Result: **$NewOrderStatus**)
      - Set **SystemStatus** = `$IteratorOrderStatus/SystemStatus`
      - Set **InternalStatusText** = `$IteratorOrderStatus/InternalStatusText`
      - Set **ExternalStatusText** = `$IteratorOrderStatus/ExternalStatusText`
      - Set **InterStatusHexCode** = `$IteratorOrderStatus/InterStatusHexCode`
      - Set **ExternalStatusHexCode** = `$IteratorOrderStatus/ExternalStatusHexCode`
      - Set **SystemStatusDescription** = `$IteratorOrderStatus/SystemStatusDescription`**
         │       2. **Add **$$NewOrderStatus
** to/from list **$FinalOrderStatusList****
         └─ **End Loop**
      7. **Commit/Save **$FinalOrderStatusList** to Database**
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. **Close current page/popup**
      10. **Show Message (Information): `Data successfully imported.`**
      11. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `File is empty.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.