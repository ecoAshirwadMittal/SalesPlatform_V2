# Microflow Detailed Specification: SUB_ResetAuctionFilters

### 📥 Inputs (Parameters)
- **$BrandList** (Type: EcoATM_MDM.Brand)
- **$CarrierList** (Type: EcoATM_MDM.Carrier)
- **$ModelList** (Type: EcoATM_MDM.Model)
- **$ModelNameList** (Type: EcoATM_MDM.ModelName)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Reset Aggregated Inventory Filters'`**
2. **Create Variable **$TimerName** = `'ResetAggregatedInventoryFilters'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. 🔄 **LOOP:** For each **$IteratorBrand** in **$BrandList**
   │ 1. **Update **$IteratorBrand**
      - Set **IsEnabledForFilter** = `false`**
   └─ **End Loop**
5. 🔄 **LOOP:** For each **$IteratorCarrier** in **$CarrierList**
   │ 1. **Update **$IteratorCarrier**
      - Set **IsEnabledForFilter** = `false`**
   └─ **End Loop**
6. 🔄 **LOOP:** For each **$IteratorModel** in **$ModelList**
   │ 1. **Update **$IteratorModel**
      - Set **IsEnabledForFilter** = `false`**
   └─ **End Loop**
7. 🔄 **LOOP:** For each **$IteratorModelName** in **$ModelNameList**
   │ 1. **Update **$IteratorModelName**
      - Set **IsEnabledForFilter** = `false`**
   └─ **End Loop**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.